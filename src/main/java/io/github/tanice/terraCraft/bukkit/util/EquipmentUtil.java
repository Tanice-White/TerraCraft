package io.github.tanice.terraCraft.bukkit.util;

import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import io.github.tanice.terraCraft.api.item.TerraItem;
import io.github.tanice.terraCraft.api.item.component.*;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.event.custom.TerraEnchantMetaLoadEvent;
import io.github.tanice.terraCraft.bukkit.event.custom.TerraItemMetaLoadEvent;
import io.github.tanice.terraCraft.bukkit.item.component.*;
import io.github.tanice.terraCraft.bukkit.util.event.TerraEvents;
import io.github.tanice.terraCraft.core.util.slot.TerraEquipmentSlot;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
     * 获取实体装备的整合 meta
     */
    public static List<TerraCalculableMeta> getActiveEquipmentMeta(LivingEntity entity) {
        List<TerraCalculableMeta> res = new ArrayList<>(12);
        for (ItemStack item : getActiveEquipmentItemStack(entity)) {
            /* 插件meta */
            res.addAll(getItemMergedTerraMeta(item));
            /* 加载原版meta */
            /* 物品本身 */
            TerraItemMetaLoadEvent itemMetaLoadEvent = TerraEvents.callAndReturn(new TerraItemMetaLoadEvent(item));
            if (itemMetaLoadEvent.getMeta() != null) res.add(itemMetaLoadEvent.getMeta());
            else {
                // TODO 原版物品meta
            }
            /* 原版附魔 */
            for (Map.Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
                TerraEnchantMetaLoadEvent enchantMetaLoadEvent = TerraEvents.callAndReturn(new TerraEnchantMetaLoadEvent(entry.getKey().toString()));
                if (enchantMetaLoadEvent.getMeta() != null) res.add(enchantMetaLoadEvent.getMeta());
                else {
                    // TODO 原版附魔meta
                }
            }

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
     * 不论自身是否是gem
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
            TerraCraftBukkit.inst().getItemManager().getLevelTemplate(levelComponent.getTemplate()).ifPresent(tmp -> {
                TerraCalculableMeta meta = tmp.getMeta();
                meta.add(tmp.getMeta(), levelComponent.getLevel());
                res.add(meta);
            });
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
