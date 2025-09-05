package io.github.tanice.terraCraft.bukkit.util.nbtapi;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import io.github.tanice.terraCraft.core.config.ConfigManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * 插件中玩家的额外信息
 */
public class NBTPlayer {
    private float externalHealth;
    /** 蓝条 */
    private double mana;
    private double maxMana;
    /** 玩家蓝量回复速度 每2tick */
    private double manaRecoverySpeed;
    /** 玩家所食用物品 */
    // TODO 只有需要计数的才会计入nbt，否则不计入
    private final Map<String, Integer> ate;

    public NBTPlayer(float externalHealth, double mana, double maxMana, double manaRecoverySpeed, Map<String, Integer> ate) {
        this.externalHealth = externalHealth;
        this.mana = mana;
        this.maxMana = maxMana;
        this.manaRecoverySpeed = manaRecoverySpeed;
        this.ate = ate;
    }

    /**
     * 创建用于玩家初始化的NBTPlayer对象
     */
    public static NBTPlayer initVanilla(Player player) {
        return new NBTPlayer(
                ConfigManager.getExternalMaxHealth(),
                ConfigManager.getOriginalMaxMana(),
                ConfigManager.getOriginalMaxMana(),
                ConfigManager.getOriginalManaRecoverySpeed(),
                new HashMap<>(0)
        );
    }

    /**
     * 获取整个插件附加的NBT
     * @param player 目标玩家
     * @return NBT数据实例
     */
    @Nullable
    public static NBTPlayer from(Player player) {
        return NBT.get(player, nbt -> {
            ReadableNBT terraCompound = nbt.getCompound("terraMeta");
            if (terraCompound == null) return null;

            ReadableNBT mapCompound = terraCompound.getCompound("ate");
            Map<String, Integer> map = new HashMap<>();
            if (mapCompound != null) {
                for (String key : mapCompound.getKeys()) map.put(key, mapCompound.getInteger(key));
            }
            return new NBTPlayer(
                    terraCompound.getFloat("externalHealth"),
                    terraCompound.getDouble("mana"),
                    terraCompound.getDouble("maxMana"),
                    terraCompound.getDouble("manaRecoverySpeed"),
                    map
            );
        });
    }

    /**
     * 玩家初始化后调用
     */
    public void apply(Player player) {
        TerraNBTAPI.setExternalHealth(player, externalHealth);
        if (!player.isHealthScaled()) {
            player.setHealthScale(20);
            player.setHealthScaled(true);
        }
        NBT.modify(player, nbt -> {
            ReadWriteNBT terraCompound = nbt.getOrCreateCompound("terraMeta");
            terraCompound.setDouble("mana", mana);
            terraCompound.setDouble("maxMana", maxMana);
            terraCompound.setFloat("externalHealth", externalHealth);
            terraCompound.setDouble("manaRecoverySpeed", manaRecoverySpeed);
            ReadWriteNBT mapCompound = terraCompound.getOrCreateCompound("ate");
            for (Map.Entry<String, Integer> entry : ate.entrySet()) {
                mapCompound.setString(entry.getKey(), String.valueOf(entry.getValue()));
            }
        });
    }

    public static void remove(LivingEntity entity) {
        NBT.modify(entity, nbt -> {nbt.removeKey("terraMeta");});
        TerraNBTAPI.removeExternalHealth(entity);
    }

    public float getExternalHealth() {
        return externalHealth;
    }

    public double getMana() {
        return mana;
    }

    public void setMana(double mana) {
        this.mana = Math.min(maxMana, mana);
    }

    public double getMaxMana() {
        return maxMana;
    }

    public void setMaxMana(double maxMana) {
        this.maxMana = maxMana;
    }

    public double getManaRecoverySpeed() {
        return manaRecoverySpeed;
    }

    public void setManaRecoverySpeed(double manaRecoverySpeed) {
        this.manaRecoverySpeed = manaRecoverySpeed;
    }

    public Map<String, Integer> getAte() {
        return ate;
    }

    public void eat(String terraName) {

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
