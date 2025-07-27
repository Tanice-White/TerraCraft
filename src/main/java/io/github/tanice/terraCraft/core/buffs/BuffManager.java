package io.github.tanice.terraCraft.core.buffs;

import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import io.github.tanice.terraCraft.api.buffs.TerraBuffManager;
import io.github.tanice.terraCraft.api.buffs.TerraBaseBuff;
import io.github.tanice.terraCraft.api.buffs.TerraBuffRecord;
import io.github.tanice.terraCraft.api.buffs.TerraRunnableBuff;
import io.github.tanice.terraCraft.api.plugin.TerraPlugin;
import io.github.tanice.terraCraft.api.service.TerraCacheService;
import io.github.tanice.terraCraft.api.service.TerraCached;
import io.github.tanice.terraCraft.api.utils.database.TerraDatabaseManager;
import io.github.tanice.terraCraft.api.utils.js.TerraJSEngineManager;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.events.entity.TerraAttributeUpdateEvent;
import io.github.tanice.terraCraft.bukkit.utils.events.TerraEvents;
import io.github.tanice.terraCraft.bukkit.utils.scheduler.TerraSchedulers;
import io.github.tanice.terraCraft.core.buffs.impl.AttributeBuff;
import io.github.tanice.terraCraft.core.buffs.impl.BuffRecord;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.tanice.terraCraft.core.constants.DataFolders.BUFF_FOLDER;

public final class BuffManager implements TerraBuffManager {
    private static final int BUFF_RUN_CD = 2;
    private static final int BUFF_MIN_NUM = 50;

    private final Random random;
    private final TerraPlugin plugin;

    private final ConcurrentMap<String, TerraBaseBuff> buffs;
    private final BuffProvider provider;

    /** 实体的 Buff 列表(实体id, (buff内部名, buff记录)) */
    private final ConcurrentMap<UUID, ConcurrentMap<String, TerraBuffRecord>> entityBuffs;
    /** 需要主线程执行的 buff 队列 */
    private final LinkedBlockingQueue<TerraBuffRecord> buffTaskQueue;
    /** 需要唤起属性改变事件的玩家队列 */
    private final LinkedBlockingQueue<LivingEntity> eventQueue;

    public BuffManager(TerraPlugin plugin) {
        this.random = new Random();
        this.plugin = plugin;
        this.buffs = new ConcurrentHashMap<>();
        this.provider = new BuffProvider();
        this.loadResource();

        this.entityBuffs = new ConcurrentHashMap<>();
        this.buffTaskQueue = new LinkedBlockingQueue<>();
        this.eventQueue = new LinkedBlockingQueue<>();
        TerraSchedulers.async().repeat(this::processBuffCycle, 1, BUFF_RUN_CD);
        TerraSchedulers.sync().repeat(this::doBuffEffects, 1, BUFF_RUN_CD);
    }

    public void reload() {
        buffs.clear();
        entityBuffs.clear();
        buffTaskQueue.clear();
        eventQueue.clear();
    }

    public void unload() {
        buffs.clear();
        entityBuffs.clear();
        buffTaskQueue.clear();
        eventQueue.clear();
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
    public Collection<String> filterBuffs(Collection<String> buffNames, String name) {
        if (name == null) return getBuffNames();
        if (buffNames == null || buffNames.isEmpty()) return Collections.emptyList();
        return buffNames.stream()
                .filter(buff -> buff.startsWith(name))
                .collect(Collectors.toList());
    }

    /**
     * 需要延迟添加
     * 玩家登录后从数据库初始化对应的信息
     */
    @Override
    public void loadPlayerBuffs(Player player) {
        if (!TerraCraftBukkit.inst().getConfigManager().useMysql()) return;

        TerraCraftBukkit.inst().getDatabaseManager().loadPlayerBuffRecords(player.getUniqueId().toString())
                .thenAccept(records -> {
                    if (records.isEmpty()) return;
                    ConcurrentMap<String, TerraBuffRecord> playerBuff;
                    for (TerraBuffRecord record : records) {
                        playerBuff = entityBuffs.computeIfAbsent(record.getId(), k -> new ConcurrentHashMap<>(BUFF_MIN_NUM));
                        playerBuff.put(record.getBuff().getName(), record);
                    }
                    /* Debug */
                    if (TerraCraftBukkit.inst().getConfigManager().isDebug()) {
                        StringBuilder s = new StringBuilder("Player " + player.getName() + " joined. Syncing buffs: ");
                        for (TerraBuffRecord record : records) s.append(record.getBuff().getDisplayName()).append(" ");
                        TerraCraftLogger.debug(TerraCraftLogger.DebugLevel.BUFF, s.toString());
                    }
                });
        TerraEvents.call(new TerraAttributeUpdateEvent(player));
    }

    /**
     * 玩家退出时清理所有Timer Buff任务
     */
    @Override
    public void SaveAndClearPlayerBuffs(Player player) {
        if (TerraCraftBukkit.inst().getConfigManager().useMysql()) {
            ConcurrentMap<String, TerraBuffRecord> res = entityBuffs.get(player.getUniqueId());
            if (res == null) return;
            TerraCraftBukkit.inst().getDatabaseManager().saveBuffRecords(res.values());

            if (TerraCraftBukkit.inst().getConfigManager().isDebug()) {
                StringBuilder s = new StringBuilder("Player " + player.getName() + " left. Saving buffs: ");
                for (TerraBuffRecord r : res.values()) s.append(r.toString()).append(" ");
                TerraCraftLogger.debug(TerraCraftLogger.DebugLevel.BUFF, s.toString());
            }
        }
        entityBuffs.remove(player.getUniqueId());
    }

    /**
     * 储存所有的玩家记录
     * 非玩家的记录不保存
     */
    @Override
    public void saveAllPlayerRecords() {
        TerraCacheService cacheService = TerraCraftBukkit.inst().getCacheService();
        TerraDatabaseManager databaseManager = TerraCraftBukkit.inst().getDatabaseManager();

        for (Map.Entry<UUID, ConcurrentMap<String, TerraBuffRecord>> entry : entityBuffs.entrySet()) {
            if (cacheService.get(entry.getKey()).isPlayer())
                databaseManager.saveBuffRecords(entry.getValue().values());
        }
    }

    /**
     * 为实体增加 buff
     * 完成后请触发 EntityAttributeChangeEvent 事件
     * @return  buff 是否被触发
     */
    @Override
    public boolean activateBuff(LivingEntity entity, TerraBaseBuff buff) {
        return activateBuff(entity,buff, false);
    }

    /**
     * 为实体增加 buff
     * 完成后请触发 EntityAttributeChangeEvent 事件
     */
    @Override
    public boolean activateBuff(LivingEntity entity, TerraBaseBuff buff, boolean isPermanent) {
        if (random.nextDouble() > buff.getChance()) return false;
        UUID uuid = entity.getUniqueId();
        if (TerraCraftBukkit.inst().getCacheService().get(uuid) == null) return false;

        ConcurrentMap<String, TerraBuffRecord> ebs = this.entityBuffs.computeIfAbsent(uuid, id -> new ConcurrentHashMap<>(BUFF_MIN_NUM));

        /* 创建或更新 Buff 记录 */
        ebs.compute(buff.getName(), (name, existingRecord) -> {
            if (existingRecord != null) {
                existingRecord.merge(buff, isPermanent);
                return existingRecord;

            } else return new BuffRecord(entity, buff, isPermanent);
        });
        return true;
    }

    /**
     * 为实体激活一组非永久的 buff
     * 完成后请触发 EntityAttributeChangeEvent 事件
     * @return 是否有 buff 被触发
     */
    @Override
    public boolean activateBuffs(LivingEntity entity, Collection<TerraBaseBuff> buffs) {
        return activateBuffs(entity, buffs, false);
    }

    /**
     * 为实体激活一组 buff
     * 完成后请触发 EntityAttributeChangeEvent 事件
     */
    @Override
    public boolean activateBuffs(LivingEntity entity, Collection<TerraBaseBuff> buffs, boolean isPermanent) {
        UUID uuid = entity.getUniqueId();
        if (TerraCraftBukkit.inst().getCacheService().get(uuid) == null) return false;

        ConcurrentMap<String, TerraBuffRecord> entityBuffs;
        boolean flag = isPermanent;
        for (TerraBaseBuff bPDC : buffs) {
            if (!isPermanent && random.nextDouble() > bPDC.getChance()) continue;
            entityBuffs = this.entityBuffs.computeIfAbsent(uuid, id -> new ConcurrentHashMap<>(BUFF_MIN_NUM));
            /* 创建或更新 Buff 记录 */
            entityBuffs.compute(bPDC.getName(), (name, existingRecord) -> {
                if (existingRecord != null) {
                    existingRecord.merge(bPDC, isPermanent);
                    return existingRecord;

                } else return new BuffRecord(entity, bPDC, isPermanent);
            });
            flag = true;
        }
        return flag;
    }

    /**
     * 批量清除实体的 buff 效果
     * 完成后请触发 EntityAttributeChangeEvent 事件
     */
    @Override
    public void deactivateBuffs(LivingEntity entity, Collection<TerraBaseBuff> buffs) {
        UUID uuid = entity.getUniqueId();
        TerraCacheService cacheService = TerraCraftBukkit.inst().getCacheService();

        TerraCached cached = cacheService.get(uuid);
        if (cached == null || !cached.isValid()) {
            entityBuffs.remove(uuid);
            return;
        }
        ConcurrentMap<String, TerraBuffRecord> ebs = this.entityBuffs.get(uuid);
        if (ebs == null || ebs.isEmpty()) return;

        for (TerraBaseBuff baseBuff : buffs) ebs.remove(baseBuff.getName());
    }

    /**
     * 去除实体的所有 buff
     * 完成后请触发 EntityAttributeChangeEvent 事件
     */
    @Override
    public void deactivateEntityBuffs(LivingEntity entity) {
        entityBuffs.remove(entity.getUniqueId());
    }

    /**
     * 获取实体的有效 buff
     */
    @Override
    public List<TerraCalculableMeta> getEntityActiveBuffs(LivingEntity entity) {
        UUID uuid = entity.getUniqueId();
        TerraCacheService cacheService = TerraCraftBukkit.inst().getCacheService();
        TerraCached cached = cacheService.get(uuid);

        if (cached == null || !cached.isValid()) return List.of();

        ConcurrentMap<String, TerraBuffRecord> records = entityBuffs.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>(BUFF_MIN_NUM));
        if (records.isEmpty()) return List.of();

        List<TerraCalculableMeta> res = new ArrayList<>(records.size() * 2);

        for (TerraBuffRecord r : records.values()) {
            if (!r.isRunnable()) res.add(((AttributeBuff) r.getBuff()).getMeta());
        }
        return res;
    }

    /**
     * 执行 buff 效果
     */
    private void doBuffEffects() {
        List<TerraBuffRecord> buffsToExecute = new ArrayList<>();
        List<LivingEntity> attributeToChange = new ArrayList<>();

        TerraCacheService cacheService = TerraCraftBukkit.inst().getCacheService();
        TerraJSEngineManager engineManager = TerraCraftBukkit.inst().getJSEngineManager();

        /* 主线程执行buff效果 可以设置负载，过高则分给下 RUN_CD - 1 个tick */
        buffTaskQueue.drainTo(buffsToExecute);
        TerraCached cached;
        TerraBaseBuff baseBuff;
        for (TerraBuffRecord record : buffsToExecute) {
            cached = cacheService.get(record.getId());
            if (cached == null) continue;
            if (cached.isValid()) {
                baseBuff = record.getBuff();
                if (baseBuff instanceof TerraRunnableBuff b) engineManager.executeFunction(b.getFileName(), cached.getEntity());
            }
        }
        /* 唤起事件 - 改变玩家属性 */
        eventQueue.drainTo(attributeToChange);
        for (LivingEntity e : attributeToChange) TerraEvents.call(new TerraAttributeUpdateEvent(e));
    }


    /**
     * 处理 buff 生命周期
     */
    private void processBuffCycle() {
        Map.Entry<UUID, ConcurrentMap<String, TerraBuffRecord>> buffMapEntry;
        UUID uid;
        ConcurrentMap<String, TerraBuffRecord> entityBuffs;
        TerraBuffRecord record;
        boolean changed;
        TerraCacheService cacheService = TerraCraftBukkit.inst().getCacheService();

        for (Iterator<Map.Entry<UUID, ConcurrentMap<String, TerraBuffRecord>>> it = this.entityBuffs.entrySet().iterator(); it.hasNext(); ) {
            /* 初始化 */
            buffMapEntry = it.next();
            uid = buffMapEntry.getKey();
            entityBuffs = buffMapEntry.getValue();
            changed = false;

            /* 检查实体有效性 */
            TerraCached cached = cacheService.get(uid);
            if (cached == null || !cached.isValid()) {
                it.remove();
                continue;
            }

            if (entityBuffs == null || entityBuffs.isEmpty()) continue;
            for (Iterator<TerraBuffRecord> innerIt = entityBuffs.values().iterator(); innerIt.hasNext();) {
                record = innerIt.next();

                /* 执行冷却 永久类不会减少 duration */
                record.cooldown(BUFF_RUN_CD);

                /* 检查持续时间 - 永续类不减少持续时间 */
                if (record.getDurationCounter() < 0) {
                    changed = true;
                    innerIt.remove();
                }
                /* 检查触发cd */
                if (record.isTimer() && record.getCooldownCounter() <= 0) {
                    if (buffTaskQueue.offer(record)) record.reloadCooldown();
                    else TerraCraftLogger.error("buffTaskQueue overflow!");
                }
            }
            if (changed) eventQueue.offer(cached.getEntity());
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
                    else provider.createBuff(name, file).ifPresent(b -> buffs.put(name, b));
                }
            });
            TerraCraftLogger.success("Loaded " + provider.getTotal() + " buffs in total, including " + provider.getValid() + " valid buffs and " + provider.getOther() + " invalid active_section buffs.");
        } catch (IOException e) {
            TerraCraftLogger.error("Failed to load buffs from " + buffDir.toAbsolutePath() + " " + e.getMessage());
        }
    }
}
