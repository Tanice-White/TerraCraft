package io.github.tanice.terraCraft.core.skills;

import io.github.tanice.terraCraft.api.attribute.AttributeType;
import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import io.github.tanice.terraCraft.api.items.TerraItem;
import io.github.tanice.terraCraft.api.plugin.TerraPlugin;
import io.github.tanice.terraCraft.api.skills.TerraSkillCarrier;
import io.github.tanice.terraCraft.api.skills.TerraSkillManager;
import io.github.tanice.terraCraft.api.skills.Trigger;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.events.entity.TerraSkillUpdateEvent;
import io.github.tanice.terraCraft.bukkit.utils.EquipmentUtil;
import io.github.tanice.terraCraft.bukkit.utils.events.TerraEvents;
import io.github.tanice.terraCraft.bukkit.utils.scheduler.TerraSchedulers;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import io.github.tanice.terraCraft.core.skills.helper.mythicmobs.MMHelper;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static io.github.tanice.terraCraft.core.constants.ConfigKeys.*;
import static io.github.tanice.terraCraft.core.constants.DataFolders.SKILL_FOLDER;

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

    /** 技能和对应的触发器 */
    private final ConcurrentMap<String, SkillMeta> skillMap;

    /** 标记实体是否正在计算中 */
    private final ConcurrentMap<UUID, AtomicBoolean> computingFlags;
    /** 脏标记 */
    private final ConcurrentMap<UUID, AtomicBoolean> dirtyFlags;
    /** 玩家可释放的技能 */
    private final Map<UUID, EnumMap<Trigger, Set<SkillRowData>>> playerSkillMap;
    /** 玩家ID -> 技能对象 -> 下一次可释放的时间戳(毫秒) */
    private final ConcurrentMap<UUID, ConcurrentMap<String, Long>> playerSkillCooldowns;

    // TODO mana恢复和PlayerData维护


    public SkillManager(TerraPlugin plugin) {
        this.plugin = plugin;
        this.skillMap = new ConcurrentHashMap<>();
        this.computingFlags = new ConcurrentHashMap<>();
        this.dirtyFlags = new ConcurrentHashMap<>();
        this.playerSkillMap = new ConcurrentHashMap<>();
        this.playerSkillCooldowns = new ConcurrentHashMap<>();
        this.loadResourceFiles();

        TerraSchedulers.async().repeat(this::cleanup, 2, CLEAN_UP_CD);
        TerraEvents.subscribe(TerraSkillUpdateEvent.class).priority(EventPriority.HIGH).ignoreCancelled(true).handler(event -> {
            this.updateAvailableSkills(event.getEntity());
        }).register();
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
        UUID playerId = player.getUniqueId();

        /* 获取玩家所有可用技能 */
        EnumMap<Trigger, Set<SkillRowData>> triggerSkillMap = playerSkillMap.get(playerId);
        if (triggerSkillMap == null || triggerSkillMap.isEmpty()) return;
        /* 获取指定触发器对应的技能集合 */
        Set<SkillRowData> skills = triggerSkillMap.get(trigger);
        if (skills == null || skills.isEmpty()) return;

        long currentTime = System.currentTimeMillis();
        TerraCalculableMeta meta = TerraCraftBukkit.inst().getEntityAttributeManager().getAttributeCalculator(player).getMeta();

        double skillCooldown = 1 + meta.get(AttributeType.SKILL_COOLDOWN);
        if (skillCooldown < 0) skillCooldown = 0;
        double manaCost = 1 + meta.get(AttributeType.SKILL_MANA_COST);
        if (manaCost < 0) manaCost = 0;
        /* 遍历所有可用技能并释放可释放的技能 */
        for (SkillRowData skill : skills) {
            /* 可释放技能并且释放成功 */
            if (isSkillCooldownReady(player, skill.getSkillName(), currentTime) && MMHelper.castSkill(player, skill.getMythicSkillName())) {
                setSkillCooldown(player, skill, (long) (skill.getCd() * 1000L * skillCooldown) + currentTime);
                // TODO 设置蓝蚝
                if (TerraCraftBukkit.inst().getConfigManager().isDebug()) {
                    TerraCraftLogger.error("Player: " + player.getName() + " casting skill: " + skill.getSkillName()
                            + "(" + skill.getMythicSkillName() + ")" + ", mana cost: " + skill.getManaCost() * manaCost
                            + ", cd: " + skill.getCd() * skillCooldown + " * 1000 ms"
                    );
                }
            }
        }
    }

    /**
     * 手动设置技能冷却
     * @param nextAvailableTime 毫秒
     */
    public void setSkillCooldown(Player player, SkillRowData skill, long nextAvailableTime) {
        playerSkillCooldowns.compute(player.getUniqueId(), (uid, skillMap) -> {
            if (skillMap == null) skillMap = new ConcurrentHashMap<>();
            skillMap.put(skill.getSkillName(), nextAvailableTime);
            return skillMap;
        });
    }

    /**
     * 检查技能是否就绪 (只读)
     */
    public boolean isSkillCooldownReady(Player player, String skillName, long currentTime) {
        ConcurrentMap<String, Long> skillMap = playerSkillCooldowns.get(player.getUniqueId());
        if (skillMap == null || skillMap.isEmpty()) return true;

        Long nextAvailableTime = skillMap.get(skillName);
        if (nextAvailableTime == null) return true;

        return currentTime > nextAvailableTime;
    }

    /**
     * 获取技能剩余冷却时间 (只读)
     */
    public long getSkillRemainingCooldown(Player player, String skillName, long currentTime) {
        ConcurrentMap<String, Long> skillMap = playerSkillCooldowns.get(player.getUniqueId());
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
        UUID uuid = player.getUniqueId();
        dirtyFlags.computeIfAbsent(uuid, k -> new AtomicBoolean(false));
        AtomicBoolean computing = computingFlags.computeIfAbsent(uuid, k -> new AtomicBoolean(false));

        if (computing.compareAndSet(false, true)) {
            TerraSchedulers.async().run(() -> updateAvailableSkills(player));

            if (TerraCraftBukkit.inst().getConfigManager().isDebug())
                TerraCraftLogger.debug(TerraCraftLogger.DebugLevel.SKILL, "Player " + player.getName() + " available skills updated");

            /* 计算中，标记为脏 */
        } else dirtyFlags.get(uuid).set(true);
    }

    /**
     * 更新玩家可用技能
     */
    private void updateAvailableSkills(Player player) {
        UUID uuid = player.getUniqueId();
        try {
            /* 计算前清除脏标记 */
            dirtyFlags.get(uuid).set(false);
            EnumMap<Trigger, Set<SkillRowData>> tsm = playerSkillMap.computeIfAbsent(
                    uuid, k -> new EnumMap<>(Trigger.class)
            );
            /* 防止旧数据残留 */
            tsm.clear();

            SkillRowData skillRowData;
            Trigger trigger;
            for (TerraItem item : EquipmentUtil.getActiveEquipmentItem(player)) {
                if (!(item instanceof TerraSkillCarrier skillCarrier)) continue;

                for (String skillName : skillCarrier.getSkillNames()) {
                    SkillMeta skillMeta = skillMap.get(skillName);
                    if (skillMeta == null) continue;

                    skillRowData = skillMeta.skillRowData;
                    trigger = skillMeta.trigger;
                    tsm.computeIfAbsent(trigger, t -> ConcurrentHashMap.newKeySet()).add(skillRowData);
                }
            }
            /* DEBUG */
            if (TerraCraftBukkit.inst().getConfigManager().isDebug()) {
                Trigger triggerType;
                Set<SkillRowData> skillSet;
                for (Map.Entry<Trigger, Set<SkillRowData>> entry : tsm.entrySet()) {
                    triggerType = entry.getKey();
                    skillSet = entry.getValue();
                    TerraCraftLogger.debug( TerraCraftLogger.DebugLevel.SKILL, " Trigger type: " + triggerType.name());
                    for (SkillRowData skillData : skillSet) {
                        TerraCraftLogger.debug( TerraCraftLogger.DebugLevel.SKILL, skillData.getSkillName() + "(" + skillData.getMythicSkillName() + ") ");
                    }
                }
            }

        } finally {
            computingFlags.get(uuid).set(false);
            /* 如果期间有新请求，继续处理 */
            if (dirtyFlags.get(uuid).getAndSet(false)) {
                if (computingFlags.get(uuid).compareAndSet(false, true)) {
                    TerraSchedulers.async().run(() -> updateAvailableSkills(player));
                }
            }
        }
    }

    /**
     * 定期清理过期记录
     */
    private void cleanup() {
        long expiredThreshold = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(REMAIN_TIME);
        playerSkillCooldowns.forEach((uuid, skillMap) -> {
            Entity e = Bukkit.getEntity(uuid);
            if (e == null || !e.isValid()) {
                playerSkillCooldowns.remove(uuid, skillMap);
                return;
            }
            skillMap.entrySet().removeIf(entry -> {
                long nextAvailableTime = entry.getValue();
                return nextAvailableTime <= expiredThreshold;
            });
            /* 玩家没有技能条目则删除 */
            if (skillMap.isEmpty()) playerSkillCooldowns.remove(uuid, skillMap);
        });
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
                                s -> new SkillMeta(new SkillRowData(k, cfg), trigger));
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
        SkillRowData skillRowData;
        Trigger trigger;

        SkillMeta(SkillRowData skillRowData, Trigger trigger) {
            this.skillRowData = skillRowData;
            this.trigger = trigger;
        }
    }
}
