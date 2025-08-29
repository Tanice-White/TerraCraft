package io.github.tanice.terraCraft.core.buff;

import io.github.tanice.terraCraft.api.buff.TerraBuffManager;
import io.github.tanice.terraCraft.api.buff.TerraBaseBuff;
import io.github.tanice.terraCraft.api.buff.TerraBuffRecord;
import io.github.tanice.terraCraft.api.buff.TerraRunnableBuff;
import io.github.tanice.terraCraft.api.plugin.TerraPlugin;
import io.github.tanice.terraCraft.api.database.TerraDatabaseManager;
import io.github.tanice.terraCraft.api.js.TerraJSEngineManager;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.event.entity.TerraAttributeUpdateEvent;
import io.github.tanice.terraCraft.bukkit.item.component.BuffComponent;
import io.github.tanice.terraCraft.bukkit.util.EquipmentUtil;
import io.github.tanice.terraCraft.bukkit.util.TerraWeakReference;
import io.github.tanice.terraCraft.bukkit.util.event.TerraEvents;
import io.github.tanice.terraCraft.bukkit.util.nbtapi.NBTBuff;
import io.github.tanice.terraCraft.bukkit.util.scheduler.TerraSchedulers;
import io.github.tanice.terraCraft.core.config.ConfigManager;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.tanice.terraCraft.core.constant.DataFolders.BUFF_FOLDER;

public final class BuffManager implements TerraBuffManager {
    private static final int BUFF_RUN_CD = 2;
    private static final int CLEAN_UP_CD = 7;

    private final Random random;
    private final TerraPlugin plugin;

    private final ConcurrentMap<String, TerraBaseBuff> buffs;
    private final BuffProvider provider;

    /** 实体的 Buff 列表(实体id, (buff内部名, buff记录)) */
    private final ConcurrentMap<TerraWeakReference, ConcurrentMap<String, TerraBuffRecord>> entityBuffs;
    /** 需要主线程执行的 buff 队列 */
    private final LinkedBlockingQueue<TerraBuffRecord> buffTaskQueue;

    public BuffManager(TerraPlugin plugin) {
        this.random = new Random();
        this.plugin = plugin;
        this.buffs = new ConcurrentHashMap<>();
        this.provider = new BuffProvider();

        this.entityBuffs = new ConcurrentHashMap<>();
        this.buffTaskQueue = new LinkedBlockingQueue<>();

        this.loadResource();
        TerraSchedulers.async().repeat(this::processBuffLifeCycle, 1, BUFF_RUN_CD);
        TerraSchedulers.sync().repeat(this::doBuffEffects, 1, BUFF_RUN_CD);

        TerraSchedulers.async().repeat(this::cleanup, 3, CLEAN_UP_CD);
    }

    public void reload() {
        saveAllPlayerRecords();
        provider.reload();
        buffs.clear();
        entityBuffs.clear();
        buffTaskQueue.clear();
        loadResource();
    }

    public void unload() {
        saveAllPlayerRecords();
        buffs.clear();
        entityBuffs.clear();
        buffTaskQueue.clear();
    }

    @Override
    public Collection<String> getBuffNames() {
        return Collections.unmodifiableCollection(buffs.keySet());
    }

    @Override
    public Optional<TerraBaseBuff> getBuff(String name) {
        TerraBaseBuff buff = buffs.get(name);
        if (buff == null) return Optional.empty();
        else return Optional.of(buff.clone());
    }

    @Override
    public Collection<String> filterBuffs(String name) {
        return this.buffs.keySet().stream().filter(buff -> buff.startsWith(name)).collect(Collectors.toList());
    }

    @Override
    public void loadPlayerBuffs(Player player) {
        if (!ConfigManager.useMysql()) return;

        TerraCraftBukkit.inst().getDatabaseManager().loadPlayerBuffRecords(player.getUniqueId().toString())
                .thenAccept(records -> {
                    if (records.isEmpty()) return;
                    ConcurrentMap<String, TerraBuffRecord> playerBuff;
                    for (TerraBuffRecord record : records) {
                        playerBuff = entityBuffs.computeIfAbsent(record.getEntityReference(), k -> new ConcurrentHashMap<>());
                        playerBuff.put(record.getBuff().getName(), record);
                    }
                    /* Debug */
                    if (ConfigManager.isDebug()) {
                        StringBuilder s = new StringBuilder("Player " + player.getName() + " joined. Syncing buffs: ");
                        for (TerraBuffRecord record : records) s.append(record.getBuff().getDisplayName()).append(" ");
                        TerraCraftLogger.debug(TerraCraftLogger.DebugLevel.BUFF, s.toString());
                    }
                });
        TerraEvents.callSync(new TerraAttributeUpdateEvent(player));
    }

    @Override
    public void SaveAndClearPlayerBuffs(Player player) {
        TerraWeakReference reference = new TerraWeakReference(player);
        if (ConfigManager.useMysql()) {
            ConcurrentMap<String, TerraBuffRecord> res = entityBuffs.get(reference);
            if (res == null) return;
            TerraCraftBukkit.inst().getDatabaseManager().saveBuffRecords(res.values());

            if (ConfigManager.isDebug()) {
                StringBuilder s = new StringBuilder("Player " + player.getName() + " left. Saving buffs: ");
                for (TerraBuffRecord r : res.values()) s.append(r.toString()).append(" ");
                TerraCraftLogger.debug(TerraCraftLogger.DebugLevel.BUFF, s.toString());
            }
        }
        entityBuffs.remove(reference);
    }

    @Override
    public void saveAllPlayerRecords() {
        TerraDatabaseManager databaseManager = TerraCraftBukkit.inst().getDatabaseManager();
        LivingEntity e;
        for (Map.Entry<TerraWeakReference, ConcurrentMap<String, TerraBuffRecord>> entry : entityBuffs.entrySet()) {
            e = entry.getKey().get();
            if (e instanceof Player && e.isValid()) databaseManager.saveBuffRecords(entry.getValue().values());
        }
    }

    @Override
    public void activateBuff(LivingEntity entity, TerraBaseBuff buff) {
        activateBuffs(entity, Collections.singleton(buff), false);
    }

    @Override
    public void activateHoldBuffs(LivingEntity entity) {
        List<TerraBaseBuff> buffs = new ArrayList<>();
        for (ItemStack item : EquipmentUtil.getActiveEquipmentItemStack(entity)) {
            BuffComponent buffComponent = BuffComponent.from(item);
            if (buffComponent == null) continue;
            buffs.addAll(buffComponent.getHold().stream().map(NBTBuff::getAsTerraBuff).filter(Objects::nonNull).toList());
        }
        activateBuffs(entity, buffs, true);
    }

    @Override
    public void activateBuffs(LivingEntity entity, Collection<TerraBaseBuff> buffs) {
        activateBuffs(entity, buffs, false);
    }

    /** core */
    @Override
    public void activateBuffs(LivingEntity entity, Collection<TerraBaseBuff> buffs, boolean ignoreChance) {
        if (entity == null || !entity.isValid()) return;
        TerraSchedulers.async().run(() -> addBuffsToEntity(entity, buffs, ignoreChance));
    }

    @Override
    public void deactivateBuff(LivingEntity entity, TerraBaseBuff buff) {
        deactivateBuffs(entity, Collections.singleton(buff));
    }

    @Override
    public void deactivateBuffs(LivingEntity entity, Collection<TerraBaseBuff> buffs) {
        if (entity == null || !entity.isValid() || buffs == null || buffs.isEmpty()) return;

        entityBuffs.computeIfPresent(new TerraWeakReference(entity), (uuid, ebs) -> {
            for (TerraBaseBuff baseBuff : buffs) ebs.remove(baseBuff.getName());
            TerraEvents.callSync(new TerraAttributeUpdateEvent(entity));
            return ebs;
        });
    }

    @Override
    public void deactivateEntityBuffs(LivingEntity entity) {
        entityBuffs.remove(new TerraWeakReference(entity));
        TerraEvents.callSync(new TerraAttributeUpdateEvent(entity));
    }

    /**
     * 获取实体的有效 buff Meta
     * 不排除即将remove的buff记录 --防止出现空挡
     */
    @Override
    public List<TerraBaseBuff> getEntityActiveBuffs(LivingEntity entity) {
        if (entity == null || !entity.isValid()) return List.of();

        ConcurrentMap<String, TerraBuffRecord> records = entityBuffs.get(new TerraWeakReference(entity));
        if (records == null || records.isEmpty()) return List.of();

        List<TerraBaseBuff> res = new ArrayList<>(records.size());
        for (TerraBuffRecord r : records.values()) {
            if (!r.isTimer()) res.add(r.getBuff());
        }
        return res;
    }

    /**
     * 获取实体buff记录
     * 标记移除的也会获取到
     */
    @Override
    public Collection<TerraBuffRecord> getEntityActiveBuffRecords(LivingEntity entity) {
        if (entity == null || !entity.isValid()) return List.of();
        ConcurrentMap<String, TerraBuffRecord> records = entityBuffs.get(new TerraWeakReference(entity));
        if (records == null || records.isEmpty()) return List.of();
        return records.values();
    }

    /**
     * 同一个异步线程上，更新 hold_buff
     * @param entity 目标实体
     */
    private void ActivateHoldBuffsSingleThread(LivingEntity entity) {
        List<TerraBaseBuff> buffs = new ArrayList<>();
        for (ItemStack item : EquipmentUtil.getActiveEquipmentItemStack(entity)) {
            BuffComponent buffComponent = BuffComponent.from(item);
            if (buffComponent == null) continue;
            buffs.addAll(buffComponent.getHold().stream().map(NBTBuff::getAsTerraBuff).filter(Objects::nonNull).toList());
        }
        addBuffsToEntity(entity, buffs, true);
    }

    /**
     * 执行 buff 效果
     */
    private void doBuffEffects() {
        List<TerraBuffRecord> buffsToExecute = new ArrayList<>();
        TerraJSEngineManager engineManager = TerraCraftBukkit.inst().getJSEngineManager();
        /* 主线程执行buff效果 可以设置负载，过高则分给下 RUN_CD - 1 个tick */
        buffTaskQueue.drainTo(buffsToExecute);
        LivingEntity e;
        TerraBaseBuff baseBuff;
        for (TerraBuffRecord record : buffsToExecute) {
            e = record.getEntityReference().get();
            if (e == null || !e.isValid()) continue;
            baseBuff = record.getBuff();
            if (baseBuff instanceof TerraRunnableBuff b) engineManager.executeFunction(b.getFileName(), e);
        }
    }


    /**
     * 异步处理 buff 生命周期
     */
    private void processBuffLifeCycle() {
        ConcurrentMap<String, TerraBuffRecord> ebs;
        TerraBuffRecord record;
        boolean tagged;

        for (Map.Entry<TerraWeakReference, ConcurrentMap<String, TerraBuffRecord>> buffMapEntry : this.entityBuffs.entrySet()) {
            LivingEntity entity = buffMapEntry.getKey().get();
            if (entity == null || !entity.isValid()) continue;

            ebs = buffMapEntry.getValue();
            if (ebs == null || ebs.isEmpty()) continue;

            tagged = false; // 是否标记了需要移除的buff
            for (Iterator<TerraBuffRecord> innerIt = ebs.values().iterator(); innerIt.hasNext(); ) {
                record = innerIt.next();
                if (record.isToRemove()) {
                    innerIt.remove();
                    continue;
                }
                record.cooldown(BUFF_RUN_CD);
                /* 检查持续时间 */
                if (record.getDurationCounter() < 0) {
                    record.setToRemove(true);
                    tagged = true;
                    continue;
                }
                /* 检查触发cd */
                if (record.isTimer() && record.getCooldownCounter() <= 0) {
                    if (buffTaskQueue.offer(record)) record.reloadCooldown();
                    else TerraCraftLogger.error("buffTaskQueue overflow!");
                }
            }
            if (tagged) ActivateHoldBuffsSingleThread(entity);
        }
    }

    /**
     * 较为耗时, 异步执行
     */
    private void addBuffsToEntity(LivingEntity entity, Collection<TerraBaseBuff> buffs, boolean ignoreChance) {
        if (entity == null || !entity.isValid()) return;
        boolean changed = false;
        for (TerraBaseBuff buff : buffs) {
            if (!ignoreChance && random.nextDouble() > buff.getChance()) continue;
            ConcurrentMap<String, TerraBuffRecord> ebs = entityBuffs.computeIfAbsent(new TerraWeakReference(entity), id -> new ConcurrentHashMap<>());
            boolean skip = false;
            for (TerraBuffRecord r : ebs.values()) {
                if (r.isToRemove()) continue; // 忽略即将移除的
                // 存在冲突 或 被现有buff覆盖，均跳过当前buff
                if (buff.mutexWith(r.getBuff().getName()) || r.getBuff().canOverride(buff.getName())) {
                    skip = true;
                    break;
                }
            }
            if (skip) continue;
            // 移除被当前buff覆盖的"有效buff"
            ebs.entrySet().removeIf(entry -> {
                TerraBuffRecord r = entry.getValue();
                return !r.isToRemove() && buff.canOverride(entry.getKey());
            });
            /* 执行增加 */
            ebs.compute(buff.getName(), (name, existingRecord) -> {
                if (existingRecord != null) {
                    existingRecord.merge(buff);
                    return existingRecord;

                } else return new BuffRecord(entity, buff);
            });
            changed = true;
        }
        if (changed) {
            if (ConfigManager.isDebug())
                TerraCraftLogger.debug(TerraCraftLogger.DebugLevel.BUFF, "Entity: " + entity.getName() + " buffs updated");
            TerraEvents.callSync(new TerraAttributeUpdateEvent(entity));
        }
    }

    private void loadResource() {
        Path buffDir = plugin.getDataFolder().toPath().resolve(BUFF_FOLDER);
        if (!Files.exists(buffDir) || !Files.isDirectory(buffDir)) {
            TerraCraftLogger.error("Buffer directory validation failed: " + buffDir + " is not a valid directory");
            return;
        }
        try (Stream<Path> files = Files.list(buffDir)) {
            files.forEach(file -> {
                String fileName = file.getFileName().toString();
                if (fileName.endsWith(".yml")) {
                    ConfigurationSection section = YamlConfiguration.loadConfiguration(file.toFile());
                    for (String k : section.getKeys(false)) {
                        if (buffs.containsKey(k)) {
                            TerraCraftLogger.error("Existing buff: " + k);
                            continue;
                        }
                        provider.createBuff(k, section.getConfigurationSection(k)).ifPresent(b -> buffs.put(k, b));
                    }
                } else if (fileName.endsWith(".js")) {
                    String name = fileName.substring(0, fileName.lastIndexOf('.'));
                    if (buffs.containsKey(name)) TerraCraftLogger.error("Existing buff: " + name);
                    else provider.createBuff(name, file).ifPresent(b -> buffs.put(b.getName(), b));
                }
            });
            TerraCraftLogger.success("Loaded " + provider.getTotal() + " buffs in total, including " + provider.getValid() + " valid buffs and " + provider.getOther() + " invalid active_section buffs.");
        } catch (IOException e) {
            TerraCraftLogger.error("Failed to load buffs from " + buffDir.toAbsolutePath() + " " + e.getMessage());
        }
    }

    private void cleanup() {
        Iterator<Map.Entry<TerraWeakReference, ConcurrentMap<String, TerraBuffRecord>>> it = entityBuffs.entrySet().iterator();
        Map.Entry<TerraWeakReference, ConcurrentMap<String, TerraBuffRecord>> entry;
        LivingEntity entity;
        while (it.hasNext()) {
            entry = it.next();
            entity = entry.getKey().get();
            if (entity != null && entity.isValid()) continue;
            entityBuffs.remove(entry.getKey());
        }
    }
}
