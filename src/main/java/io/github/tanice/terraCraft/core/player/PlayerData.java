package io.github.tanice.terraCraft.core.player;

import io.github.tanice.terraCraft.api.player.TerraPlayerData;
import io.github.tanice.terraCraft.core.config.ConfigManager;
import io.github.tanice.terraCraft.core.util.logger.TerraCraftLogger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.github.tanice.terraCraft.core.constant.ConfigKeys.*;

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
    private final Map<String, Integer> ate;

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
        return new PlayerData(
                player.getUniqueId().toString(),
                ConfigManager.getOriginalMaxHealth(),
                ConfigManager.getOriginalMaxHealth(),
                ConfigManager.getOriginalMaxMana(),
                ConfigManager.getOriginalMaxMana(),
                ConfigManager.getOriginalManaRecoverySpeed(),
                new HashMap<>(0)
        );
    }

    public static PlayerData newFrom(ConfigurationSection cfg) {
        return new PlayerData(
                null,
                cfg.getDouble(HEALTH, 0D),
                cfg.getDouble(MAX_HEALTH, 0D),
                cfg.getDouble(MANA, 0D),
                cfg.getDouble(MAX_MANA, 0D),
                cfg.getDouble(MANA_RECOVERY_SPEED, 0D),
                new HashMap<>(0)
        );
    }

    /**
     * 玩家初始化后调用
     */
    @Override
    public void apply() {
        Entity e = Bukkit.getEntity(UUID.fromString(uuid));
        if (!(e instanceof Player player) || !e.isValid()) {
            TerraCraftLogger.error("Player " + uuid + " does not exist, cancelling PlayerData synchronization");
            return;
        }

        // TODO 使用NBTAPI的额外profile设置最大生命值
        // AttributeAPI.setOriBaseAttr(player, Attribute.MAX_HEALTH, maxHealth);
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

    /**
     * 合并两个PlayerData，即属性改变
     */
    @Override
    public void merge(TerraPlayerData playerData) {
        this.health += playerData.getHealth();
        this.maxHealth += playerData.getMaxHealth();
        this.mana += playerData.getMana();
        this.maxMana += playerData.getMaxMana();
        this.manaRecoverySpeed += playerData.getManaRecoverySpeed();
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
    public void setMana(double mana) {
        this.mana = Math.min(maxMana, mana);
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
