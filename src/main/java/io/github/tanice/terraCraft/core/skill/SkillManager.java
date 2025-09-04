package io.github.tanice.terraCraft.core.skill;

import io.github.tanice.terraCraft.api.attribute.AttributeType;
import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import io.github.tanice.terraCraft.api.attribute.TerraEntityAttributeManager;
import io.github.tanice.terraCraft.api.plugin.TerraPlugin;
import io.github.tanice.terraCraft.api.skill.TerraSkillManager;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.item.component.SkillComponent;
import io.github.tanice.terraCraft.bukkit.util.EquipmentUtil;
import io.github.tanice.terraCraft.bukkit.util.TerraWeakReference;
import io.github.tanice.terraCraft.bukkit.util.scheduler.TerraSchedulers;
import io.github.tanice.terraCraft.core.config.ConfigManager;
import io.github.tanice.terraCraft.core.util.logger.TerraCraftLogger;
import io.github.tanice.terraCraft.core.util.helper.mythicmobs.MMHelper;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static io.github.tanice.terraCraft.core.constant.ConfigKeys.*;
import static io.github.tanice.terraCraft.core.constant.DataFolders.SKILL_FOLDER;

/**
 * 管理技能和技能冷却
 * 监听式释放
 */
public final class SkillManager implements TerraSkillManager {
    private final TerraPlugin plugin;
    /** 清理间隔时间(ticks) */
    private static final long CLEAN_UP_CD = 20;
    /** 未使用记录保留时间(分钟) */
    private static final long REMAIN_TIME = 5;
    /** 蓝量恢复间隔 */
    private static final long RECOVERY_CD = 2;

    private final TerraEntityAttributeManager attributeManager;

    /** 技能和对应的触发器 */
    private final ConcurrentMap<String, SkillMeta> skillMap;

    /** 标记实体是否正在计算中 */
    private final ConcurrentMap<TerraWeakReference, AtomicBoolean> computingFlags;
    /** 脏标记 */
    private final ConcurrentMap<TerraWeakReference, AtomicBoolean> dirtyFlags;
    /** 玩家可释放的技能 */
    private final Map<TerraWeakReference, EnumMap<Trigger, Set<SkillMetaData>>> playerSkillMap;
    /** 玩家ID -> 技能对象 -> 下一次可释放的时间戳(毫秒) */
    private final ConcurrentMap<TerraWeakReference, ConcurrentMap<String, Long>> playerSkillCooldowns;

    /** 玩家蓝量 */
    private final ConcurrentMap<TerraWeakReference, Double> playerMana;

    public SkillManager(TerraPlugin plugin) {
        this.plugin = plugin;
        this.attributeManager = TerraCraftBukkit.inst().getEntityAttributeManager();

        this.skillMap = new ConcurrentHashMap<>();
        this.computingFlags = new ConcurrentHashMap<>();
        this.dirtyFlags = new ConcurrentHashMap<>();
        this.playerSkillMap = new ConcurrentHashMap<>();
        this.playerSkillCooldowns = new ConcurrentHashMap<>();
        this.playerMana = new ConcurrentHashMap<>();
        this.loadResourceFiles();

        TerraSchedulers.async().repeat(this::cleanup, 2, CLEAN_UP_CD);
        TerraSchedulers.async().repeat(this::recoverMana, 2, RECOVERY_CD);
    }

    public void reload() {
        skillMap.clear();
        computingFlags.clear();
        dirtyFlags.clear();
        playerSkillMap.clear();
        playerSkillCooldowns.clear();
        this.loadResourceFiles();
    }

    public void unload() {
        skillMap.clear();
        computingFlags.clear();
        dirtyFlags.clear();
        playerSkillMap.clear();
        playerSkillCooldowns.clear();
    }

    /**
     * 释放技能(通过监听玩家事件)
     */
    public void castSkill(Player player, Trigger trigger) {
        TerraSchedulers.async().run(() -> asyncCastSkills(player, trigger));
    }

    /**
     * 手动设置技能冷却
     * @param nextAvailableTime 毫秒
     */
    public void setSkillCooldown(Player player, SkillMetaData skill, long nextAvailableTime) {
        playerSkillCooldowns.compute(new TerraWeakReference(player), (uid, skillMap) -> {
            if (skillMap == null) skillMap = new ConcurrentHashMap<>();
            skillMap.put(skill.getSkillName(), nextAvailableTime);
            return skillMap;
        });
    }

    /**
     * 检查技能是否就绪 (只读)
     */
    public boolean isSkillCooldownReady(Player player, String skillName, long currentTime) {
        ConcurrentMap<String, Long> skillMap = playerSkillCooldowns.get(new TerraWeakReference(player));
        if (skillMap == null || skillMap.isEmpty()) return true;

        Long nextAvailableTime = skillMap.get(skillName);
        if (nextAvailableTime == null) return true;

        return currentTime > nextAvailableTime;
    }

    /**
     * 获取技能剩余冷却时间 (只读)
     */
    public long getSkillRemainingCooldown(Player player, String skillName, long currentTime) {
        ConcurrentMap<String, Long> skillMap = playerSkillCooldowns.get(new TerraWeakReference(player));
        if (skillMap == null) return 0;

        Long nextAvailableTime = skillMap.get(skillName);
        if (nextAvailableTime == null) return 0;

        if (currentTime >= nextAvailableTime) return 0;
        return nextAvailableTime - currentTime;
    }

    /**
     * 提交玩家技能更新
     */
    public void updatePlayerSkills(Player player) {
        TerraWeakReference reference = new TerraWeakReference(player);
        dirtyFlags.computeIfAbsent(reference, k -> new AtomicBoolean(false));
        AtomicBoolean computing = computingFlags.computeIfAbsent(reference, k -> new AtomicBoolean(false));

        if (computing.compareAndSet(false, true)) {
            TerraSchedulers.async().run(() -> updateAvailableSkills(player));

            if (ConfigManager.isDebug())
                TerraCraftLogger.debug(TerraCraftLogger.DebugLevel.SKILL, "Player " + player.getName() + " available skills updated");

            /* 计算中，标记为脏 */
        } else dirtyFlags.get(reference).set(true);
    }

    /**
     * 异步技能调用
     */
    private void asyncCastSkills(Player player, Trigger trigger) {
        TerraWeakReference reference = new TerraWeakReference(player);

        /* 获取玩家所有可用技能 */
        EnumMap<Trigger, Set<SkillMetaData>> triggerSkillMap = playerSkillMap.get(reference);
        if (triggerSkillMap == null || triggerSkillMap.isEmpty()) return;
        /* 获取指定触发器对应的技能集合 */
        Set<SkillMetaData> skills = triggerSkillMap.get(trigger);
        if (skills == null || skills.isEmpty()) return;

        long currentTime = System.currentTimeMillis();
        TerraCalculableMeta meta = attributeManager.getAttributeCalculator(player).getMeta();

        /* 遍历所有可用技能并释放可释放的技能 */
        for (SkillMetaData skill : skills) {
            /* 可释放技能并且释放成功 */
            if (isSkillCooldownReady(player, skill.getSkillName(), currentTime) && MMHelper.castSkill(player, skill.getMythicSkillName())) {
                /* 技能至少相间一个tick */
                double skillCooldown = 1 + meta.get(AttributeType.SKILL_COOLDOWN);
                if (skillCooldown < 0) skillCooldown = 0;
                if (skill.getCd() > 1) {
                    setSkillCooldown(player, skill, (long) (skill.getCd() * 1000L * skillCooldown) + currentTime);
                }
                double manaCost = 1 + meta.get(AttributeType.SKILL_MANA_COST);
                playerMana.computeIfPresent(reference, (key, mana) -> mana + skill.getManaCost() * (manaCost > 0 ? manaCost : 0));
                if (ConfigManager.isDebug()) {
                    TerraCraftLogger.error("Player: " + player.getName() + " casting skill: " + skill.getSkillName()
                            + "(" + skill.getMythicSkillName() + ")" + ", mana cost: " + skill.getManaCost() * manaCost
                            + ", cd: " + skill.getCd() * skillCooldown + " * 1000 ms"
                    );
                }
            }
        }
    }

    /**
     * 更新玩家可用技能
     */
    private void updateAvailableSkills(Player player) {
        TerraWeakReference reference = new TerraWeakReference(player);
        try {
            /* 计算前清除脏标记 */
            dirtyFlags.get(reference).set(false);
            EnumMap<Trigger, Set<SkillMetaData>> tsm = playerSkillMap.computeIfAbsent(
                    reference, k -> new EnumMap<>(Trigger.class)
            );
            /* 防止旧数据残留 */
            tsm.clear();

            SkillMetaData skillRowData;
            Trigger trigger;
            SkillComponent skillComponent;
            for (ItemStack item : EquipmentUtil.getActiveEquipmentItemStack(player)) {
                skillComponent = SkillComponent.from(item);
                if (skillComponent == null) continue;

                for (String skillName : skillComponent.getSkills()) {
                    SkillMeta skillMeta = skillMap.get(skillName);
                    if (skillMeta == null) continue;

                    skillRowData = skillMeta.skillMetaData;
                    trigger = skillMeta.trigger;
                    tsm.computeIfAbsent(trigger, t -> ConcurrentHashMap.newKeySet()).add(skillRowData);
                }
            }
            /* DEBUG */
            if (ConfigManager.isDebug()) {
                Trigger triggerType;
                Set<SkillMetaData> skillSet;
                for (Map.Entry<Trigger, Set<SkillMetaData>> entry : tsm.entrySet()) {
                    triggerType = entry.getKey();
                    skillSet = entry.getValue();
                    TerraCraftLogger.debug( TerraCraftLogger.DebugLevel.SKILL, " Trigger type: " + triggerType.name());
                    for (SkillMetaData skillMetaData : skillSet) {
                        TerraCraftLogger.debug( TerraCraftLogger.DebugLevel.SKILL, skillMetaData.getSkillName() + "(" + skillMetaData.getMythicSkillName() + ") ");
                    }
                }
            }

        } finally {
            computingFlags.get(reference).set(false);
            /* 如果期间有新请求，继续处理 */
            if (dirtyFlags.get(reference).getAndSet(false)) {
                if (computingFlags.get(reference).compareAndSet(false, true)) {
                    TerraSchedulers.async().run(() -> updateAvailableSkills(player));
                }
            }
        }
    }

    /**
     * 玩家蓝量恢复
     */
    private void recoverMana() {
        playerMana.replaceAll((ref, mana) -> {
            LivingEntity p = ref.get();
            if (p == null || !p.isValid()) return mana;
            return mana + attributeManager.getAttributeCalculator(p).getMeta().get(AttributeType.MANA_RECOVERY_SPEED);
        });
    }

    /**
     * 定期清理过期记录
     */
    private void cleanup() {
        long expiredThreshold = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(REMAIN_TIME);
        Iterator<Map.Entry<TerraWeakReference, ConcurrentMap<String, Long>>> it = playerSkillCooldowns.entrySet().iterator();
        Map.Entry<TerraWeakReference, ConcurrentMap<String, Long>> entry;
        TerraWeakReference reference;
        ConcurrentMap<String, Long> cooldownMap;
        LivingEntity entity;
        while (it.hasNext()) {
            entry = it.next();
            reference = entry.getKey();
            cooldownMap = entry.getValue();
            entity = reference.get();

            cooldownMap.entrySet().removeIf(skillEntry -> skillEntry.getValue() <= expiredThreshold);
            if (entity == null || !entity.isValid() || !(entity instanceof Player) || cooldownMap.isEmpty()) {
                it.remove();
                playerSkillMap.remove(reference);
                computingFlags.remove(reference);
                dirtyFlags.remove(reference);
            }
        }
    }

    private void loadResourceFiles(){
        AtomicInteger total = new AtomicInteger();
        Path buffDir = plugin.getDataFolder().toPath().resolve(SKILL_FOLDER);
        if (!Files.exists(buffDir) || !Files.isDirectory(buffDir)) return;
        try (Stream<Path> files = Files.list(buffDir)) {
            files.forEach(file -> {
                String fileName = file.getFileName().toString();
                ConfigurationSection subsection;
                if (fileName.endsWith(".yml")) {
                    ConfigurationSection section = YamlConfiguration.loadConfiguration(file.toFile());
                    for (String k : section.getKeys(false)) {
                        subsection = section.getConfigurationSection(k);
                        if (subsection == null) continue;
                        final ConfigurationSection cfg = subsection;
                        String t = cfg.getString(TRIGGER);
                        if (t == null) continue;
                        Trigger trigger;
                        try {
                            trigger = Trigger.valueOf(t.toUpperCase());
                        } catch (IllegalArgumentException e) {
                            TerraCraftLogger.error("Skill: The trigger for " + k + " does not exist!");
                            continue;
                        }
                        skillMap.computeIfAbsent(k,
                                s -> new SkillMeta(new SkillMetaData(k, cfg), trigger));
                        total.getAndIncrement();
                    }
                }
            });
            TerraCraftLogger.success("Loaded " + total.get() + " skills");
        } catch (IOException e) {
            TerraCraftLogger.error("Failed to load Skills file: " + e.getMessage());
        }
    }

    private static class SkillMeta {
        SkillMetaData skillMetaData;
        Trigger trigger;

        SkillMeta(SkillMetaData skillMetaData, Trigger trigger) {
            this.skillMetaData = skillMetaData;
            this.trigger = trigger;
        }
    }
}
