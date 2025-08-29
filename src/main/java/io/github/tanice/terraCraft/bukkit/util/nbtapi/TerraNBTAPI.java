package io.github.tanice.terraCraft.bukkit.util.nbtapi;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import io.github.tanice.terraCraft.bukkit.util.version.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.util.version.ServerVersion;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static io.github.tanice.terraCraft.api.item.component.TerraBaseComponent.MINECRAFT_PREFIX;

public final class TerraNBTAPI {
    // TODO 玩家属性 AttributeAPI

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

}
