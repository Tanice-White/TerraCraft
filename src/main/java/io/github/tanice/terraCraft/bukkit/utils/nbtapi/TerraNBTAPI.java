package io.github.tanice.terraCraft.bukkit.utils.nbtapi;

import io.github.tanice.terraCraft.api.items.TerraBaseItem;

public final class TerraNBTAPI {

    /**
     * 设置自定义NBT标签
     */
    public static boolean setCustomNBT(TerraBaseItem item, String key, String value) {
        if (item == null || key == null || value == null) return false;

        try {
            String fullKey = TAG_CUSTOM_PREFIX + key;
            modifyNBT(item, nbt -> nbt.setString(fullKey, value));
            return true;
        } catch (Exception e) {
            TerraCraftBukkit.inst().getLogger().warning("Failed to set custom NBT: " + e.getMessage());
            return false;
        }
    }

    /**
     * 获取自定义NBT标签
     */
    public static String getCustomNBT(TerraBaseItem item, String key) {
        if (item == null || key == null) return null;

        String fullKey = TAG_CUSTOM_PREFIX + key;
        return getNBTValue(item, nbt -> nbt.getString(fullKey));
    }

    /**
     * 移除所有自定义NBT标签
     */
    public static void removeAllCustomNBT(TerraBaseItem item) {
        if (item == null) return;

        modifyNBT(item, nbt -> {
            for (String key : nbt.getKeys()) {
                if (key.startsWith(TAG_CUSTOM_PREFIX)) {
                    nbt.removeKey(key);
                }
            }
        });
    }


    // TODO 玩家属性 AttributeAPI
}
