package io.github.tanice.terraCraft.bukkit.util.nbtapi;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import io.github.tanice.terraCraft.core.config.ConfigManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * 插件中玩家的额外信息
 */
public class NBTPlayer {

    public static final NBTPlayer ORIGINAL = new NBTPlayer(
            ConfigManager.getOriginalExternalMaxHealth(),
            0,
            ConfigManager.getOriginalMaxMana(),
            new NBTMeta(ConfigManager.getOriginalPlayerMeta())
    );

    private float externalHealth;
    /** 蓝条 */
    private double mana;
    private double maxMana;
    private NBTMeta meta;

    public NBTPlayer(float externalHealth, double mana, double maxMana, NBTMeta meta) {
        this.externalHealth = externalHealth;
        this.mana = mana;
        this.maxMana = maxMana;
        this.meta = meta;
    }

    /**
     * 获取整个插件附加的NBT
     * @param player 目标玩家
     * @return NBT数据实例, 没有则会返回默认的初始值
     */
    public static NBTPlayer from(Player player) {
        return NBT.getPersistentData(player, nbt -> {
            ReadableNBT terraCompound = nbt.getCompound("terraMeta");
            if (terraCompound == null) return ORIGINAL;

            return new NBTPlayer(
                    terraCompound.getFloat("externalHealth"),
                    terraCompound.getDouble("mana"),
                    terraCompound.getDouble("maxMana"),
                    NBTMeta.fromNBT(terraCompound)
            );
        });
    }

    /**
     * 属性写入nbt
     */
    public void apply(Player player) {
        TerraNBTAPI.setExternalHealth(player, externalHealth);
        if (!player.isHealthScaled()) {
            player.setHealthScale(20);
            player.setHealthScaled(true);
        }
        NBT.modifyPersistentData(player, nbt -> {
            ReadWriteNBT terraCompound = nbt.getOrCreateCompound("terraMeta");
            terraCompound.setDouble("mana", mana);
            terraCompound.setDouble("maxMana", maxMana);
            terraCompound.setFloat("externalHealth", externalHealth);
            meta.addToCompound(terraCompound);
        });
    }

    public static void remove(LivingEntity entity) {
        NBT.modifyPersistentData(entity, nbt -> {nbt.removeKey("terraMeta");});
        TerraNBTAPI.removeExternalHealth(entity);
    }

    public float getExternalHealth() {
        return externalHealth;
    }

    public void setExternalHealth(float externalHealth) {
        this.externalHealth = externalHealth;
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

    public TerraCalculableMeta getMeta() {
        return meta.getMeta();
    }

    public void setMeta(TerraCalculableMeta meta) {
        this.meta = new NBTMeta(meta);
    }

    public NBTPlayer clone() {
        try {
            NBTPlayer clone = (NBTPlayer) super.clone();
            clone.externalHealth = this.externalHealth;
            clone.mana = this.mana;
            clone.maxMana = this.maxMana;
            clone.meta = this.meta.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
