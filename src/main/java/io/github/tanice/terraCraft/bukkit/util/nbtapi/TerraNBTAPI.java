package io.github.tanice.terraCraft.bukkit.util.nbtapi;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import io.github.tanice.terraCraft.api.item.component.vanilla.TerraAttributeModifiersComponent;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static io.github.tanice.terraCraft.api.item.component.TerraBaseComponent.MINECRAFT_PREFIX;

public final class TerraNBTAPI {

    /**
     * 判断物品是否有盾牌属性
     */
    public static boolean isShield(@Nullable ItemStack item) {
        if (item == null || item.isEmpty()) return false;
        /* 检测组件 */
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_5)) {
            AtomicBoolean isShield = new AtomicBoolean(false);
            NBT.getComponents(item, nbt -> {
                /* 有组件则 */
                if (nbt.resolveCompound(MINECRAFT_PREFIX + "blocks_attacks") != null) {
                    isShield.set(true);
                    return;
                }
                isShield.set(nbt.resolveCompound("!" + MINECRAFT_PREFIX + "blocks_attacks") == null && item.getType() == Material.SHIELD);
            });
            return isShield.get();
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
            AtomicInteger damagePerBlock = new AtomicInteger(1);
            NBT.getComponents(item, nbt -> {
                ReadableNBT compound = nbt.resolveCompound(MINECRAFT_PREFIX + "tool");
                if (compound == null) return;
                if (compound.hasTag("damage_per_block")) damagePerBlock.set( compound.getInteger("damage_per_block"));
            });
            return damagePerBlock.get();
        }
        return 1;
    }

    /**
     * 获取原版weapon组件中的 damage_per_attack
     */
    public static int damagePerAttack(@Nullable ItemStack item) {
        if (item == null || item.isEmpty()) return 0;
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_5)) {
            AtomicInteger damagePerAttack = new AtomicInteger(1);
            NBT.getComponents(item, nbt -> {
                ReadableNBT component = nbt.resolveCompound(MINECRAFT_PREFIX + "weapon");
                if (component == null) return;
                if (component.hasTag("item_damage_per_attack")) damagePerAttack.set(component.getInteger("item_damage_per_attack"));
            });
            return damagePerAttack.get();
        }
        return 1;
    }

    /**
     * 获取原版的break_sound
     */
    public static String breakSound(ItemStack item) {
        AtomicReference<String> res = new AtomicReference<>("minecraft:entity.item.break");
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_5)) {
            NBT.getComponents(item, nbt -> {
                ReadableNBT compound = nbt.getCompound(MINECRAFT_PREFIX + "break_sound");
                if (compound != null) res.set(compound.getString("sound_id"));
            });
        }
        return res.get();
    }

    // TODO 玩家属性

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
                if (id == null || id.isEmpty() || !id.equals(BukkitAttribute.MAX_HEALTH.getBukkitAttribute().toString())) continue;
                found = true;
                setExternalHealth(compound.getCompoundList("modifiers").addCompound(), externalHealth);
                break;
            }
            if (found && ConfigManager.isDebug())
                TerraCraftLogger.debug(TerraCraftLogger.DebugLevel.ATTRIBUTE, "Successfully set external health " + externalHealth + "to Living " + entity.getName());


            if (!found) {
                TerraCraftLogger.info("Cannot find generic max_health attribute for entity " + entity.getName() + " try to generate");

                ReadWriteNBT compound = nbt.getCompoundList("attributes").addCompound();
                compound.setString("id", BukkitAttribute.MAX_HEALTH.getBukkitAttribute().toString());
                compound.setDouble("base", 20D);
                setExternalHealth(compound.getCompoundList("modifiers").addCompound(), externalHealth);

                TerraCraftLogger.success("Successfully generated max_health attribute for entity " + entity.getName());
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
     * 获取mana恢复速度
     */
    public static double getManaRecoverySpeed(LivingEntity entity) {
        return NBT.get(entity, nbt -> {
            Double v = nbt.resolveOrNull("terraMeta.manaRecoverySpeed", Double.class);
            if (v == null) return ConfigManager.getOriginalManaRecoverySpeed();
            return v;
        });
    }

    /**
     * 判断实体是否在地面
     * @param entity 目标实体
     * @return 在地面返回 true 否则 false
     */
    public static boolean isOnGround(Entity entity) {
        AtomicBoolean onGround = new AtomicBoolean(false);
        NBT.get(entity, nbt -> {onGround.set(nbt.getBoolean("OnGround"));});
        return onGround.get();
    }

    private static void setExternalHealth(ReadWriteNBT compound, float externalHealth) {
        compound.setString("id", new TerraNamespaceKey("external_health").get());
        compound.setFloat("amount", externalHealth);
        compound.setString("operation", TerraAttributeModifiersComponent.Operation.ADD_VALUE.name().toLowerCase());
    }

}
