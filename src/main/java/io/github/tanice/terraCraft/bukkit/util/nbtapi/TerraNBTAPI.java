package io.github.tanice.terraCraft.bukkit.util.nbtapi;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import io.github.tanice.terraCraft.api.item.component.vanilla.TerraAttributeModifiersComponent;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.util.adapter.BukkitAttribute;
import io.github.tanice.terraCraft.bukkit.util.version.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.util.version.ServerVersion;
import io.github.tanice.terraCraft.core.config.ConfigManager;
import io.github.tanice.terraCraft.core.util.logger.TerraCraftLogger;
import io.github.tanice.terraCraft.core.util.namespace.TerraNamespaceKey;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Objects;

import static io.github.tanice.terraCraft.api.item.component.TerraBaseComponent.MINECRAFT_PREFIX;

public final class TerraNBTAPI {

    public static final String TERRA_ENTITY_COMPONENT = "terraMeta";

    /**
     * 判断物品是否有盾牌属性
     */
    public static boolean isShield(@Nullable ItemStack item) {
        if (item == null || item.isEmpty()) return false;
        /* 检测组件 */
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_5)) {
            return NBT.getComponents(item, nbt -> {
                /* 有组件 */
                if (nbt.resolveCompound(MINECRAFT_PREFIX + "blocks_attacks") != null) return true;
                return nbt.resolveCompound("!" + MINECRAFT_PREFIX + "blocks_attacks") == null && item.getType() == Material.SHIELD;
            });
        }
        /* 看数据类型 */
        return item.getType() == Material.SHIELD;
    }

    /**
     * 获取原版tool组件中的 damage_per_block
     */
    public static int damagePerBlock(@Nullable ItemStack item) {
        if (item == null || item.isEmpty()) return 0;
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            return NBT.getComponents(item, nbt -> {
                ReadableNBT compound = nbt.resolveCompound(MINECRAFT_PREFIX + "tool");
                if (compound == null) return 1;
                if (compound.hasTag("damage_per_block")) return compound.getInteger("damage_per_block");
                return 1;
            });
        }
        return 1;
    }

    /**
     * 获取原版weapon组件中的 damage_per_attack
     */
    public static int damagePerAttack(@Nullable ItemStack item) {
        if (item == null || item.isEmpty()) return 0;
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_5)) {
            return NBT.getComponents(item, nbt -> {
                ReadableNBT component = nbt.resolveCompound(MINECRAFT_PREFIX + "weapon");
                if (component == null) return 1;
                if (component.hasTag("item_damage_per_attack")) return component.getInteger("item_damage_per_attack");
                return 1;
            });
        }
        return 1;
    }

    /**
     * 获取原版的break_sound
     */
    public static String breakSound(ItemStack item) {
        String res = "minecraft:entity.item.break";
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_5)) {
            return NBT.getComponents(item, nbt -> {
                ReadableNBT compound = nbt.getCompound(MINECRAFT_PREFIX + "break_sound");
                if (compound != null) return compound.getString("sound_id");
                return res;
            });
        }
        return res;
    }

    /**
     * 设置生物额外生命值
     * @param entity 生物
     * @param externalHealth 额外的生命值（add）
     */
    public static void setExternalHealth(LivingEntity entity, float externalHealth) {
        NBT.modify(entity, nbt -> {
            String id;
            boolean found = false;
            for (ReadWriteNBT compound : nbt.getCompoundList("attributes")) {
                id = compound.getString("id");
                if (id == null || id.isEmpty() || !id.equals(BukkitAttribute.MAX_HEALTH.getAttributeKey().asString())) continue;
                found = true;
                setExternalHealth(compound.getCompoundList("modifiers").addCompound(), externalHealth);
                break;
            }
            if (found && ConfigManager.isDebug())
                TerraCraftLogger.debug(TerraCraftLogger.DebugLevel.ATTRIBUTE, "Successfully set external health " + externalHealth + "to Living " + entity.getName());


            if (!found) {
                TerraCraftLogger.info("Cannot find generic max_health attribute for " + entity.getName() + " try to generate");

                ReadWriteNBT compound = nbt.getCompoundList("attributes").addCompound();
                compound.setString("id", BukkitAttribute.MAX_HEALTH.getAttributeKey().asString());
                compound.setDouble("base", 20D);
                setExternalHealth(compound.getCompoundList("modifiers").addCompound(), externalHealth);

                TerraCraftLogger.success("Successfully generated max_health attribute for " + entity.getName());
            }
        });
    }

    /**
     * 移除生物额外生命值
     */
    public static void removeExternalHealth(LivingEntity entity) {
        NBT.modify(entity, nbt -> {
            String id;
            for (ReadWriteNBT compound : nbt.getCompoundList("attributes")) {
                id = compound.getString("id");
                if (id == null || id.isEmpty() || !id.equals(BukkitAttribute.MAX_HEALTH.getBukkitAttribute().toString())) continue;
                compound.getCompoundList("modifiers").removeIf(sub -> Objects.equals(sub.getString("id"), new TerraNamespaceKey("external_health").get()));
            }
        });
    }

    /**
     * 判断实体是否在地面
     * @param entity 目标实体
     * @return 在地面返回 true 否则 false
     */
    public static boolean isOnGround(Entity entity) {
        return NBT.get(entity, nbt -> {return nbt.getBoolean("OnGround");});
    }

    // 玩家属性

    /**
     * 获取玩家mana恢复速度
     */
    public static double getManaRecoverySpeed(LivingEntity entity) {
        return NBT.getPersistentData(entity, nbt -> {
            Double v = nbt.resolveOrNull(TERRA_ENTITY_COMPONENT + ".manaRecoverySpeed", Double.class);
            return v == null ? -1 : v;
        });
    }

    /**
     * 设置玩家mana恢复速度
     */
    public static void setManaRecoverySpeed(LivingEntity entity, double v) {
        NBT.modifyPersistentData(entity, nbt -> {
            nbt.getOrCreateCompound(TERRA_ENTITY_COMPONENT).setDouble("manaRecoverySpeed", v);
        });
    }

    /**
     * 获取玩家蓝量
     */
    public static double getMana(LivingEntity entity) {
        return NBT.getPersistentData(entity, nbt -> {
            Double v = nbt.resolveOrNull(TERRA_ENTITY_COMPONENT + ".mana", Double.class);
            return v == null ? -1 : v;
        });
    }

    /**
     * 设置玩家蓝量
     */
    public static void setMana(LivingEntity entity, @Nullable Double v) {
        if (v == null) return;
        NBT.modifyPersistentData(entity, nbt -> {
            nbt.getOrCreateCompound(TERRA_ENTITY_COMPONENT).setDouble("mana", v);
        });
    }

    /**
     * 获取玩家最大mana
     */
    public static double getMaxMana(LivingEntity entity) {
        return NBT.getPersistentData(entity, nbt -> {
            Double v = nbt.resolveOrNull(TERRA_ENTITY_COMPONENT + ".maxMana", Double.class);
            return v == null ? ConfigManager.getOriginalMaxMana() : v;
        });
    }

    /**
     * 设置玩家最大mana
     */
    public static void setMaxMana(LivingEntity entity, double v) {
        NBT.modifyPersistentData(entity, nbt -> {
            nbt.getOrCreateCompound(TERRA_ENTITY_COMPONENT).setDouble("maxMana", v);
        });
    }

    private static void setExternalHealth(ReadWriteNBT compound, float externalHealth) {
        compound.setString("id", new TerraNamespaceKey("external_health").get());
        compound.setFloat("amount", externalHealth);
        compound.setString("operation", TerraAttributeModifiersComponent.Operation.ADD_VALUE.name().toLowerCase());
    }

}
