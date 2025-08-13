package io.github.tanice.terraCraft.bukkit.utils.nbtapi;

import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

/**
 * 向游戏内实体或者物品注入PDC
 */
public final class PDCAPI {

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
