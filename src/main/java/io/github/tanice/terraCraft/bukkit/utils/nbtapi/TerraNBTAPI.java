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

    private TerraNBTAPI() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * 获取物品内部名称
     */
    public static String getItemName(TerraBaseItem item) {
        if (item == null) return null;
        return getNBTValue(item, nbt -> nbt.getString(TAG_TERRA_NAME));
    }

    /**
     * 设置物品内部名称
     */
    public static void setItemName(TerraBaseItem item, String name) {
        if (item == null) return;
        modifyNBT(item, nbt -> nbt.setString(TAG_TERRA_NAME, name));
    }

    /**
     * 获取物品的更新code
     */
    public static Integer getCode(TerraBaseItem item) {
        if (item == null) return null;
        return getNBTValue(item, nbt -> {
            if (nbt.hasTag(TAG_CODE)) return nbt.getInteger(TAG_CODE);
            return null;
        });
    }

    /**
     * 设置物品的更新code
     */
    public static void setCode(TerraBaseItem item, int code) {
        if (item == null) return;
        modifyNBT(item, nbt -> nbt.setInteger(TAG_CODE, code));
    }

    /**
     * 获取物品最大耐久
     */
    public static Integer getMaxDamage(TerraBaseItem item) {
        if (item == null) return null;
        return getNBTValue(item, nbt -> {
            if (nbt.hasTag(TAG_MAX_DAMAGE)) return nbt.getInteger(TAG_MAX_DAMAGE);
            return null;
        });
    }

    /**
     * 设置物品最大耐久
     */
    public static void setMaxDamage(TerraBaseItem item, int maxDamage) {
        if (item == null) return;
        modifyNBT(item, nbt -> nbt.setInteger(TAG_MAX_DAMAGE, maxDamage));
    }

    /**
     * 获取物品当前耐久
     */
    public static Integer getCurrentDamage(TerraBaseItem item) {
        if (item == null) return null;

        return getNBTValue(item, nbt -> {
            if (nbt.hasTag(TAG_DAMAGE)) {
                return nbt.getInteger(TAG_DAMAGE);
            }
            return null;
        });
    }

    /**
     * 设置物品当前耐久
     */
    public static void setCurrentDamage(TerraBaseItem item, int currentDamage) {
        if (item == null) return;

        modifyNBT(item, nbt -> nbt.setInteger(TAG_DAMAGE, currentDamage));
    }

    /**
     * 获取物品品质名称
     */
    public static String getQualityName(TerraBaseItem item) {
        if (item == null) return null;

        return getNBTValue(item, nbt -> nbt.getString(TAG_QUALITY));
    }

    /**
     * 设置物品品质名称
     */
    public static void setQualityName(TerraBaseItem item, String qualityName) {
        if (item == null) return;

        modifyNBT(item, nbt -> nbt.setString(TAG_QUALITY, qualityName));
    }

    /**
     * 获取宝石列表
     */
    public static String[] getGems(TerraBaseItem item) {
        if (item == null) return null;

        return getNBTValue(item, nbt -> {
            if (nbt.hasTag(TAG_GEMS)) {
                return nbt.getStringArray(TAG_GEMS);
            }
            return null;
        });
    }

    /**
     * 设置宝石列表
     */
    public static void setGems(TerraBaseItem item, String[] gems) {
        if (item == null) return;

        modifyNBT(item, nbt -> nbt.setStringArray(TAG_GEMS, gems));
    }

    /**
     * 获取物品等级
     */
    public static Integer getLevel(TerraBaseItem item) {
        if (item == null) return 0;

        return getNBTValue(item, nbt -> {
            if (nbt.hasTag(TAG_LEVEL)) {
                return nbt.getInteger(TAG_LEVEL);
            }
            return 0;
        });
    }

    /**
     * 设置物品等级
     */
    public static void setLevel(TerraBaseItem item, int level) {
        if (item == null) return;

        modifyNBT(item, nbt -> nbt.setInteger(TAG_LEVEL, level));
    }

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

    /**
     * 根据服务器版本选择合适的NBT修改方法
     */
    private static void modifyNBT(TerraBaseItem item, Consumer<ReadWriteNBT> consumer) {
        if (item == null || modifier == null) return;

        // 使用正确的版本判断方法和NBT操作API
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt -> consumer.accept(nbt));
        } else {
            NBT.modify(item, nbt -> modifier.modify(nbt));
        }
    }

    /**
     * 根据服务器版本选择合适的NBT获取方法
     */
    private static <T> T getNBTValue(TerraBaseItem item, NBTGetter<T> getter) {
        if (item == null || getter == null) return null;

        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            return NBT.getComponents(item, getter::get);
        } else {
            return NBT.get(item, getter::get);
        }
    }

    // TODO 玩家属性 AttributeAPI
}
