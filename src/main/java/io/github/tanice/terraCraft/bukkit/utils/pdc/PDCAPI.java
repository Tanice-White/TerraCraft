package io.github.tanice.terraCraft.bukkit.utils.pdc;

import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;

/**
 * 向游戏内实体或者物品注入PDC
 */
public final class PDCAPI {
    /**
     * 获取物品内部名称
     */
    public static String getItemName(ItemStack item) {
        if (item == null) return null;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;
        return getItemName(item.getItemMeta());
    }

    public static String getItemName(ItemMeta meta) {
        if (meta == null) return null;
        return meta.getPersistentDataContainer().get(
                PDCKeys.TERRA_NAME,
                PersistentDataType.STRING
        );
    }

    public static void setItemName(ItemStack item, String name){
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        setItemName(meta, name);
        item.setItemMeta(meta);
    }

    public static void setItemName(ItemMeta meta, String name){
        if (meta == null) return;
        meta.getPersistentDataContainer().set(
                PDCKeys.TERRA_NAME,
                PersistentDataType.STRING,
                name
        );
    }

    /**
     * 获取物品的更新code
     */
    public static Integer getCode(ItemStack item) {
        if (item == null) return null;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;
        return getCode(meta);
    }

    public static Integer getCode(ItemMeta meta) {
        if (meta == null) return null;
        return meta.getPersistentDataContainer().get(
                PDCKeys.CODE,
                PersistentDataType.INTEGER
        );
    }

    public static void setCode(ItemStack item, int code) {
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        setCode(meta, code);
        item.setItemMeta(meta);
    }

    public static void setCode(ItemMeta meta, int code) {
        if (meta == null) return;
        meta.getPersistentDataContainer().set(
                PDCKeys.CODE,
                PersistentDataType.INTEGER,
                code
        );
    }

    /**
     * 物品耐久相关
     */
    public static Integer getMaxDamage(ItemStack item) {
        if (item == null) return null;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;
        return getMaxDamage(meta);
    }

    public static Integer getMaxDamage(ItemMeta meta) {
        if (meta == null) return null;
        return meta.getPersistentDataContainer().get(
                PDCKeys.MAX_DAMAGE,
                PersistentDataType.INTEGER
        );
    }

    public static Integer getCurrentDamage(ItemStack item) {
        if (item == null) return null;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;
        return getCurrentDamage(meta);
    }

    public static Integer getCurrentDamage(ItemMeta meta) {
        if (meta == null) return null;
        return meta.getPersistentDataContainer().get(
                PDCKeys.DAMAGE,
                PersistentDataType.INTEGER
        );
    }

    public static void setMaxDamage(ItemStack item, int maxDamage) {
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        setMaxDamage(meta, maxDamage);
        item.setItemMeta(meta);
    }

    public static void setMaxDamage(ItemMeta meta, int maxDamage) {
        if (meta == null) return;
        meta.getPersistentDataContainer().set(
                PDCKeys.MAX_DAMAGE,
                PersistentDataType.INTEGER,
                maxDamage
        );
    }

    public static void setCurrentDamage(ItemStack item, int currentDamage) {
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        setCurrentDamage(meta, currentDamage);
        item.setItemMeta(meta);
    }

    public static void setCurrentDamage(ItemMeta meta, int currentDamage) {
        if (meta == null) return;
        meta.getPersistentDataContainer().set(
                PDCKeys.DAMAGE,
                PersistentDataType.INTEGER,
                currentDamage
        );
    }

    /**
     * 品质相关
     */
    public static String getQualityName(ItemStack item) {
        if (item == null) return null;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;
        return getQualityName(meta);
    }
    
    public static String getQualityName(ItemMeta meta) {
        if (meta == null) return null;
        return meta.getPersistentDataContainer().get(
                PDCKeys.QUALITY,
                PersistentDataType.STRING
        );
    }
    
    public static void setQualityName(ItemStack item, String qualityName) {
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        setQualityName(meta, qualityName);
        item.setItemMeta(meta);
    }
    
    public static void setQualityName(ItemMeta meta, String qualityName) {
        if (meta == null) return;
        meta.getPersistentDataContainer().set(
                PDCKeys.QUALITY,
                PersistentDataType.STRING,
                qualityName
        );
    }

    /**
     * 宝石相关操作
     */
    public static String[] getGems(ItemStack item) {
        if (item == null) return null;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;
        return getGems(meta);
    }

    public static String[] getGems(ItemMeta meta) {
        if (meta == null) return null;
        return meta.getPersistentDataContainer().get(
                PDCKeys.GEMS,
                DataTypes.STRING_ARRAY
        );
    }

    public static void setGems(ItemStack item, String[] gems) {
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        setGems(meta, gems);
        item.setItemMeta(meta);
    }

    public static void setGems(ItemMeta meta, String[] gems) {
        if (meta == null) return;
        meta.getPersistentDataContainer().set(
                PDCKeys.GEMS,
                DataTypes.STRING_ARRAY,
                gems
        );
    }

    /**
     * 等级相关操作
     */
    public static Integer getLevel(ItemStack item) {
        if (item == null) return null;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return 0;
        return getLevel(meta);
    }

    public static Integer getLevel(ItemMeta meta) {
        if (meta == null) return null;
        return meta.getPersistentDataContainer().get(
                PDCKeys.LEVEL,
                PersistentDataType.INTEGER
        );
    }

    public static void setLevel(ItemStack item, int level) {
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        setLevel(meta, level);
        item.setItemMeta(meta);
    }

    public static void setLevel(ItemMeta meta, int level) {
        if (meta == null) return;
        meta.getPersistentDataContainer().set(
                PDCKeys.LEVEL,
                PersistentDataType.INTEGER,
                level
        );
    }

    /**
     * 加载额外的NBT
     */
    public static boolean setCustomNBT(ItemMeta meta, String key, String value) {
        if (meta == null) return false;

        String[] var = key.split(":");
        if (var.length != 2) return false;
        meta.getPersistentDataContainer().set(
                new NamespacedKey(var[0], var[1]),
                PersistentDataType.STRING,
                value
        );
        return true;
    }

    /**
     * 卸载NBT
     */
    public static void removeAllCustomNBT(ItemMeta meta) {
        if (meta == null) return;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        for (NamespacedKey key : container.getKeys()) {
            // TODO 支持自定义保护的 NBT 标签
            if (key.getNamespace().equalsIgnoreCase(TerraCraftBukkit.inst().getName())) continue;
            container.remove(key);
        }
    }
}
