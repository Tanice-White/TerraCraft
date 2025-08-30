package io.github.tanice.terraCraft.bukkit.util;

import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import io.github.tanice.terraCraft.api.item.TerraItem;
import io.github.tanice.terraCraft.api.item.component.*;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.event.custom.TerraItemMetaLoadEvent;
import io.github.tanice.terraCraft.bukkit.item.component.*;
import io.github.tanice.terraCraft.bukkit.util.event.TerraEvents;
import io.github.tanice.terraCraft.core.config.ConfigManager;
import io.github.tanice.terraCraft.core.util.logger.TerraCraftLogger;
import io.github.tanice.terraCraft.core.util.registry.Registry;
import io.github.tanice.terraCraft.core.util.slot.TerraEquipmentSlot;
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
        List<ItemStack> res = new ArrayList<>(6);
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
     * 获取实体装备的整合 meta
     * 如果是插件物品则不增加原版伤害值
     * 能被异步执行，所有事件需要在主线程上call!!!!!!
     */
    public static List<TerraCalculableMeta> getEntityActiveMeta(LivingEntity entity) {
        List<TerraCalculableMeta> res = new ArrayList<>(12);
        TerraInnerNameComponent nameComponent;
        TerraCalculableMeta customMeta;
        for (ItemStack item : getActiveEquipmentItemStack(entity)) {
            nameComponent = TerraNameComponent.from(item);
            /* 插件meta */
            if (nameComponent != null) res.addAll(getItemMergedTerraMeta(item));
            else {
                /* 加载原版meta */
                TerraItemMetaLoadEvent event = new TerraItemMetaLoadEvent(item);
                TerraEvents.callSync(event);
                if (event.getMeta() != null) res.add(event.getMeta());
                else {
                    customMeta = Registry.ORI_ITEM.get(item.getType().toString().toLowerCase());
                    if (customMeta != null) {
                        if (ConfigManager.isDebug()) TerraCraftLogger.debug(TerraCraftLogger.DebugLevel.REGISTRY, "found vanilla item: " + item.getType().toString().toLowerCase());
                        res.add(customMeta.clone());
                    }
                }
            }
            /* 原版附魔 */
//            for (Map.Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
//                TerraEnchantMetaLoadEvent enchantMetaLoadEvent = TerraEvents.callAndReturn(new TerraEnchantMetaLoadEvent(entry.getKey().toString()));
//                if (enchantMetaLoadEvent.getMeta() != null) res.add(enchantMetaLoadEvent.getMeta());
//                else {
//                    customMeta = Registry.ORI_ENCHANT.get(item.getType().toString());
//                    if (customMeta != null) res.add(customMeta);
//                }
//            }
        }
        // 生物体meta
        customMeta = Registry.ORI_LIVING_ENTITY.get(entity.getType().toString().toLowerCase());
        if (customMeta != null) {
            if (ConfigManager.isDebug()) TerraCraftLogger.debug(TerraCraftLogger.DebugLevel.REGISTRY, "found vanilla living entity: " + entity.getType().toString().toLowerCase());
            res.add(customMeta);
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
     * 获取item中 自身meta + levelMeta + gemMeta
     * 不判断 gem 组件
     * @param item 目标item
     * @return 融合完成的meta
     */
    public static List<TerraCalculableMeta> getItemMergedTerraMeta(ItemStack item) {
        TerraMetaComponent metaComponent = MetaComponent.from(item);
        TerraGemHolderComponent gemHolderComponent = GemHolderComponent.from(item);
        TerraLevelComponent levelComponent = LevelComponent.from(item);
        List<TerraCalculableMeta> res = new ArrayList<>();

        if (metaComponent != null) res.add(metaComponent.getMeta());
        if (gemHolderComponent != null) {
            for (ItemStack is : gemHolderComponent.getGems()) {
                metaComponent = MetaComponent.from(is);
                if (metaComponent != null) res.add(metaComponent.getMeta());
            }
        }
        if (levelComponent != null) {
            TerraCraftBukkit.inst().getItemManager().getLevelTemplate(levelComponent.getTemplate())
                    .ifPresent(tmp -> res.add(tmp.getMeta().selfMultiply(levelComponent.getLevel())));
        }
        return res;
    }

    /**
     * 非宝石
     * 通过 耐久 判断物品是否需要计入属性
     */
    private static boolean validate(ItemStack item) {
        if (item == null || item.isEmpty()) return false;
        TerraDurabilityComponent durabilityComponent = DurabilityComponent.from(item);
        return (durabilityComponent == null || !durabilityComponent.broken()) && GemComponent.from(item) == null;
    }
}
