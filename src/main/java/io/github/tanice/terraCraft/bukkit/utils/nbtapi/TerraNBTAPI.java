package io.github.tanice.terraCraft.bukkit.utils.nbtapi;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;

import java.util.function.Consumer;

public final class TerraNBTAPI {
    public static final String TAG_TERRA_NAME = "terra_ame";
    public static final String TAG_CODE = "code";
    public static final String TAG_MAX_DAMAGE = "max_damage";
    public static final String TAG_DAMAGE = "damage";
    public static final String TAG_QUALITY = "quality";
    public static final String TAG_GEMS = "gems";
    public static final String TAG_LEVEL = "level";
    public static final String TAG_CUSTOM_PREFIX = "custom_";
    public static final String COMPONENT_KEY = "components";
    public static final String MINECRAFT_PREFIX = "minecraft:";
    public static final String TAG_KEY = "PublicBukkitValues";
    public static final String DISPLAY_TAG = "display";
    public static final String LORE_TAG = "lore";

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
