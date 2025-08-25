package io.github.tanice.terraCraft.bukkit.util;

import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import io.github.tanice.terraCraft.api.item.TerraItem;
import io.github.tanice.terraCraft.api.item.component.TerraDurabilityComponent;
import io.github.tanice.terraCraft.api.item.component.TerraMetaSlotComponent;
import io.github.tanice.terraCraft.bukkit.event.custom.TerraItemMetaLoadEvent;
import io.github.tanice.terraCraft.bukkit.item.component.DurabilityComponent;
import io.github.tanice.terraCraft.bukkit.item.component.GemComponent;
import io.github.tanice.terraCraft.bukkit.item.component.MetaComponent;
import io.github.tanice.terraCraft.bukkit.item.component.SlotComponent;
import io.github.tanice.terraCraft.core.util.slot.TerraEquipmentSlot;
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
        TerraMetaSlotComponent slotComponent;
        item = equip.getItemInMainHand();
        slotComponent = SlotComponent.from(item);
        if (validate(item) && (slotComponent == null || slotComponent.activeUnder(TerraEquipmentSlot.MAINHAND))) res.add(item);
        item = equip.getItemInOffHand();
        slotComponent = SlotComponent.from(item);
        if (validate(item) && (slotComponent == null || slotComponent.activeUnder(TerraEquipmentSlot.OFFHAND))) res.add(item);
        item = equip.getHelmet();
        slotComponent = SlotComponent.from(item);
        if (validate(item) && (slotComponent == null || slotComponent.activeUnder(TerraEquipmentSlot.HEAD))) res.add(item);
        item = equip.getChestplate();
        slotComponent = SlotComponent.from(item);
        if (validate(item) && (slotComponent == null || slotComponent.activeUnder(TerraEquipmentSlot.CHEST))) res.add(item);
        item = equip.getLeggings();
        slotComponent = SlotComponent.from(item);
        if (validate(item) && (slotComponent == null || slotComponent.activeUnder(TerraEquipmentSlot.LEGS))) res.add(item);
        item = equip.getBoots();
        slotComponent = SlotComponent.from(item);
        if (validate(item) && (slotComponent == null || slotComponent.activeUnder(TerraEquipmentSlot.FEET))) res.add(item);
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
     * 非宝石
     * 通过 耐久 判断物品是否需要计入属性
     */
    public static boolean validate(ItemStack item) {
        if (item == null || item.isEmpty()) return false;
        TerraDurabilityComponent durabilityComponent = DurabilityComponent.from(item);
        return (durabilityComponent == null || !durabilityComponent.broken()) && GemComponent.from(item) == null;
    }
}
