package io.github.tanice.terraCraft.bukkit.utils;

import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import io.github.tanice.terraCraft.api.items.TerraItem;
import io.github.tanice.terraCraft.api.items.components.TerraDurabilityComponent;
import io.github.tanice.terraCraft.bukkit.events.load.TerraItemMetaLoadEvent;
import io.github.tanice.terraCraft.bukkit.items.components.DurabilityComponent;
import io.github.tanice.terraCraft.bukkit.items.components.MetaComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public final class EquipmentUtil {

    /**
     * 获取实体装备(已损坏的不计入)
     */
    public static List<ItemStack> getActiveEquipmentItemStack(LivingEntity entity) {
        EntityEquipment equip = entity.getEquipment();
        if (equip == null) return List.of();
        List<ItemStack> res = new ArrayList<>(12);
        ItemStack item;
        item = equip.getItemInMainHand();
        if (validDurability(item)) res.add(item);
        item = equip.getItemInOffHand();
        if (validDurability(item)) res.add(item);
        item = equip.getHelmet();
        if (validDurability(item)) res.add(item);
        item = equip.getChestplate();
        if (validDurability(item)) res.add(item);
        item = equip.getLeggings();
        if (validDurability(item)) res.add(item);
        item = equip.getBoots();
        if (validDurability(item)) res.add(item);
        return res;
    }

    /**
     * 获取实体装备的内部 meta
     */
    public static List<TerraCalculableMeta> getActiveEquipmentMeta(LivingEntity entity) {
        List<TerraCalculableMeta> res = new ArrayList<>(12);
        MetaComponent metaComponent;
        for (ItemStack item : getActiveEquipmentItemStack(entity)) {
            metaComponent = MetaComponent.from(item);
            if (metaComponent == null) {
                TerraItemMetaLoadEvent event = new TerraItemMetaLoadEvent(item);
                Bukkit.getPluginManager().callEvent(event);
                if (event.getMeta() != null) res.add(event.getMeta());
                // TODO 否则用原版物品默认对应的meta
            } else res.add(metaComponent.getMeta());
        }
        return res;
    }

    /**
     * 遍历目标的饰品
     */
    public static List<TerraCalculableMeta> getEffectiveAccessoryMeta(LivingEntity entity) {
        return List.of();
    }

    public static List<TerraItem> getEffectiveAccessories(LivingEntity entity) {
        return List.of();
    }

    /**
     * 通过耐久判断物品是否需要计入属性
     */
    public static boolean validDurability(ItemStack item) {
        if (item == null || item.isEmpty()) return false;
        TerraDurabilityComponent durabilityComponent = DurabilityComponent.from(item);
        return durabilityComponent == null || !durabilityComponent.broken();
    }
}
