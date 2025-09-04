package io.github.tanice.terraCraft.bukkit.util.nbtapi;

import io.github.tanice.terraCraft.core.config.ConfigManager;
import io.github.tanice.terraCraft.core.util.logger.TerraCraftLogger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * TODO NBT储存/加载方式等
 * 插件中玩家的额外信息
 */
public class NBTPlayer {
    private final UUID uuid;
    private double externalHealth;
    /** 蓝条 */
    private double mana;
    private double maxMana;
    /** 玩家蓝量回复速度 每2tick */
    private double manaRecoverySpeed;
    /** 玩家所食用物品 */
    private final Map<String, Integer> ate;

    public NBTPlayer(String uuid, double externalHealth, double mana, double maxMana, double manaRecoverySpeed, Map<String, Integer> ate) {
        this.uuid = UUID.fromString(uuid);
        this.externalHealth = externalHealth;
        this.mana = mana;
        this.maxMana = maxMana;
        this.manaRecoverySpeed = manaRecoverySpeed;
        this.ate = ate;
    }

    /**
     * 创建用于玩家初始化的NBTPlayer对象
     */
    public static NBTPlayer newFrom(Player player) {
        return new NBTPlayer(
                player.getUniqueId().toString(),
                ConfigManager.getOriginalMaxHealth(),
                ConfigManager.getOriginalMaxMana(),
                ConfigManager.getOriginalMaxMana(),
                ConfigManager.getOriginalManaRecoverySpeed(),
                new HashMap<>(0)
        );
    }

    public static NBTPlayer newFrom(ConfigurationSection cfg) {
        return new NBTPlayer(
                UUID.randomUUID().toString(),
                cfg.getDouble("external_max_health", 0D),
                cfg.getDouble("mana", 0D),
                cfg.getDouble("max_mana", 0D),
                cfg.getDouble("mana_recovery_speed", ConfigManager.getOriginalManaRecoverySpeed()),
                new HashMap<>(0)
        );
    }

    /**
     * 玩家初始化后调用
     */
    public void apply() {
        Entity e = Bukkit.getEntity(uuid);
        if (!(e instanceof Player player) || !e.isValid()) {
            TerraCraftLogger.error("Player " + uuid + " does not exist, cancelling NBTPlayer synchronization");
            return;
        }

        // TODO 使用NBTAPI
        // AttributeAPI.setOriBaseAttr(player, Attribute.MAX_HEALTH, maxHealth);
        player.setHealth(externalHealth + 20);  // TODO 基础血量

        if (!player.isHealthScaled()) {
            player.setHealthScale(20);
            player.setHealthScaled(true);
        }

        if (maxMana < 0) maxMana = 0;
        if (mana < 0) mana = 0;
        if (mana > maxMana) mana = maxMana;
        if (manaRecoverySpeed < 0) manaRecoverySpeed = 0;
    }

    /**
     * 合并两个NBTPlayer，即属性改变
     */
    public void merge(NBTPlayer np) {
        this.externalHealth += np.getExternalHealth();
        this.maxMana += np.getMaxMana();
        this.manaRecoverySpeed += np.getManaRecoverySpeed();
    }

    public UUID getUUID() {
        return uuid;
    }

    public double getExternalHealth() {
        return externalHealth;
    }

    public double getMana() {
        return mana;
    }

    public void setMana(double mana) {
        mana = Math.min(maxMana, mana);
    }

    public double getMaxMana() {
        return maxMana;
    }

    public double getManaRecoverySpeed() {
        return manaRecoverySpeed;
    }

    public Map<String, Integer> getAte() {
        return ate;
    }

    public NBTPlayer clone() {
        try {
            NBTPlayer clone = (NBTPlayer) super.clone();
            clone.externalHealth = this.externalHealth;
            clone.mana = this.mana;
            clone.maxMana = this.maxMana;
            clone.manaRecoverySpeed = this.manaRecoverySpeed;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
