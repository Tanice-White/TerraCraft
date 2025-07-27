package io.github.tanice.terraCraft.core.players;

import io.github.tanice.terraCraft.api.config.TerraConfigManager;
import io.github.tanice.terraCraft.api.players.TerraPlayerData;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.utils.attributes.AttributeAPI;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.github.tanice.terraCraft.core.constants.ConfigKeys.*;

/**
 * 玩家信息
 */
public class PlayerData implements TerraPlayerData, Cloneable {
    private String uuid;
    /** 生命值 */
    private double health;
    private double maxHealth;
    /** 蓝条 */
    private double mana;
    private double maxMana;
    /** 玩家蓝量回复速度 每tick */
    private double manaRecoverySpeed;
    /** 玩家所食用物品 */
    @Nullable
    private Map<String, Integer> ate;

    public PlayerData(String uuid, double health, double maxHealth, double mana, double maxMana, double manaRecoverySpeed, Map<String, Integer> ate) {
        this.uuid = uuid;
        this.health = health;
        this.maxHealth = maxHealth;
        this.mana = mana;
        this.maxMana = maxMana;
        this.manaRecoverySpeed = manaRecoverySpeed;
        this.ate = ate;
    }

    /**
     * 创建用于玩家初始化的PlayerData对象
     */
    public static PlayerData newFrom(Player player) {
        TerraConfigManager cm = TerraCraftBukkit.inst().getConfigManager();
        return new PlayerData(
                player.getUniqueId().toString(),
                cm.getOriginalMaxHealth(),
                cm.getOriginalMaxHealth(),
                cm.getOriginalMaxMana(),
                cm.getOriginalMaxMana(),
                cm.getOriginalManaRecoverySpeed(),
                new HashMap<>()
        );
    }

    public static PlayerData from(Player player) {
        // TODO 在 EntityAttribute 中获取 没有则在 数据库中获取
        return new PlayerData(
                player.getUniqueId().toString(),
                player.getHealth(),
                AttributeAPI.getOriBaseAttr(player, Attribute.MAX_HEALTH),
                mana,
                max_mana,
                mana_recovery_speed,
                ate
        );
    }

    public static PlayerData from(ConfigurationSection cfg) {
        return new PlayerData(
                null,
                cfg.getDouble(HEALTH, 0D),
                cfg.getDouble(MAX_HEALTH, 0D),
                cfg.getDouble(MANA, 0D),
                cfg.getDouble(MAX_MANA, 0D),
                cfg.getDouble(MANA_RECOVERY_SPEED, 0D),
                null
        );
    }

    /**
     * 合并两个PlayerData，即属性改变
     */
    public void merge(TerraPlayerData playerData) {
        this.health += playerData.getHealth();
        this.maxHealth += playerData.getMaxHealth();
        this.mana += playerData.getMana();
        this.maxMana += playerData.getMaxMana();
        this.manaRecoverySpeed += playerData.getManaRecoverySpeed();
    }

    /**
     * 玩家初始化后调用
     */
    @Override
    public void apply() {
        Player player = (Player) TerraCraftBukkit.inst().getCacheService().get(UUID.fromString(uuid)).getEntity();
        if (player == null) {
            TerraCraftLogger.error("Player " + uuid + " does not exist, cancelling PlayerData synchronization");
            return;
        }

        AttributeAPI.setOriBaseAttr(player, Attribute.MAX_HEALTH, maxHealth);
        player.setHealth(Math.min(health, maxHealth));

        if (!player.isHealthScaled()) {
            player.setHealthScale(20);
            player.setHealthScaled(true);
        }

        if (maxMana < 0) maxMana = 0;
        if (mana < 0) mana = 0;
        if (mana > maxMana) mana = maxMana;
        if (manaRecoverySpeed < 0) manaRecoverySpeed = 0;
        // TODO 设置到 PlayerDataManager 中
        /* TODO 蓝条显示 */
    }

    @Override
    public UUID getId() {
        return UUID.fromString(uuid);
    }

    @Override
    public double getHealth() {
        return this.health;
    }

    @Override
    public double getMaxHealth() {
        return this.maxHealth;
    }

    @Override
    public double getMana() {
        return this.mana;
    }

    @Override
    public double getMaxMana() {
        return this.maxMana;
    }

    @Override
    public double getManaRecoverySpeed() {
        return this.manaRecoverySpeed;
    }

    @Override
    public Map<String, Integer> getAte() {
        return this.ate;
    }

    @Override
    public PlayerData clone() {
        try {
            PlayerData clone = (PlayerData) super.clone();
            clone.uuid = this.uuid;
            clone.health = this.health;
            clone.maxHealth = this.maxHealth;
            clone.mana = this.mana;
            clone.maxMana = this.maxMana;
            clone.manaRecoverySpeed = this.manaRecoverySpeed;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
