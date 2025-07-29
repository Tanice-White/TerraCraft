package io.github.tanice.terraCraft.bukkit.utils;

import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.TerraItem;
import io.github.tanice.terraCraft.bukkit.utils.adapter.TerraBukkitAdapter;
import io.github.tanice.terraCraft.bukkit.utils.attributes.AttributeUtil;
import io.github.tanice.terraCraft.bukkit.utils.pdc.PDCAPI;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public final class EquipmentUtil {

    /**
     * 获取实体装备的内部 Item
     */
    public static List<TerraItem> getActiveEquipmentItem(LivingEntity living) {
        EntityEquipment equip = living.getEquipment();
        if (equip == null) return List.of();

        List<TerraItem> res = new ArrayList<>(12);

        ItemStack it;
        TerraBaseItem bit;
        it = equip.getItemInMainHand();
        bit = TerraBukkitAdapter.itemAdapt(it);
        if (bit instanceof TerraItem i && SlotUtil.isMainHand(i.getSlotAsString()) && isValidItem(it)) {
            res.add(i);
        }

        it = equip.getItemInOffHand();
        bit = TerraBukkitAdapter.itemAdapt(it);
        if (bit instanceof TerraItem i && SlotUtil.isOffHand(i.getSlotAsString()) && isValidItem(it)) {
            res.add(i);
        }

        it = equip.getHelmet();
        bit = TerraBukkitAdapter.itemAdapt(it);
        if (bit instanceof TerraItem i && SlotUtil.isHelmet(i.getSlotAsString()) && isValidItem(it)) {
            res.add(i);
        }

        it = equip.getChestplate();
        bit = TerraBukkitAdapter.itemAdapt(it);
        if (bit instanceof TerraItem i && SlotUtil.isChestplate(i.getSlotAsString()) && isValidItem(it)) {
            res.add(i);
        }

        it = equip.getLeggings();
        bit = TerraBukkitAdapter.itemAdapt(it);
        if (bit instanceof TerraItem i && SlotUtil.isLeggings(i.getSlotAsString()) && isValidItem(it)) {
            res.add(i);
        }

        it = equip.getBoots();
        bit = TerraBukkitAdapter.itemAdapt(it);
        if (bit instanceof TerraItem i && SlotUtil.isBoots(i.getSlotAsString()) && isValidItem(it)) {
            res.add(i);
        }

        return res;
    }

    /**
     * 获取实体装备的内部 meta
     * TODO 算上附魔
     */
    public static List<TerraCalculableMeta> getActiveEquipmentMeta(LivingEntity living) {
        EntityEquipment equip = living.getEquipment();
        if (equip == null) return List.of();

        List<TerraCalculableMeta> res = new ArrayList<>(12);
        ItemStack it;
        TerraBaseItem bit;
        TerraCalculableMeta tMeta;

        it = equip.getItemInMainHand();
        bit = TerraBukkitAdapter.itemAdapt(it);
        if (bit instanceof TerraItem terraItem) {
            if (SlotUtil.isMainHand(terraItem.getSlotAsString()) && isValidItem(it)) {
                res.add(AttributeUtil.getMergedMeta(terraItem, it));
            }
            /* 非插件物品兼容 */
        } else {
            tMeta = TerraBukkitAdapter.metaAdapt(it);
            if (tMeta != null) res.add(tMeta);
        }

        it = equip.getItemInOffHand();
        bit = TerraBukkitAdapter.itemAdapt(it);
        if (bit instanceof TerraItem terraItem) {
            if (SlotUtil.isMainHand(terraItem.getSlotAsString()) && isValidItem(it)) {
                res.add(AttributeUtil.getMergedMeta(terraItem, it));
            }
        } else {
            tMeta = TerraBukkitAdapter.metaAdapt(it);
            if (tMeta != null) res.add(tMeta);
        }

        it = equip.getHelmet();
        bit = TerraBukkitAdapter.itemAdapt(it);
        if (bit instanceof TerraItem terraItem) {
            if (SlotUtil.isMainHand(terraItem.getSlotAsString()) && isValidItem(it)) {
                res.add(AttributeUtil.getMergedMeta(terraItem, it));
            }
        } else {
            tMeta = TerraBukkitAdapter.metaAdapt(it);
            if (tMeta != null) res.add(tMeta);
        }

        it = equip.getChestplate();
        bit = TerraBukkitAdapter.itemAdapt(it);
        if (bit instanceof TerraItem terraItem) {
            if (SlotUtil.isMainHand(terraItem.getSlotAsString()) && isValidItem(it)) {
                res.add(AttributeUtil.getMergedMeta(terraItem, it));
            }
        } else {
            tMeta = TerraBukkitAdapter.metaAdapt(it);
            if (tMeta != null) res.add(tMeta);
        }

        it = equip.getLeggings();
        bit = TerraBukkitAdapter.itemAdapt(it);
        if (bit instanceof TerraItem terraItem) {
            if (SlotUtil.isMainHand(terraItem.getSlotAsString()) && isValidItem(it)) {
                res.add(AttributeUtil.getMergedMeta(terraItem, it));
            }
        } else {
            tMeta = TerraBukkitAdapter.metaAdapt(it);
            if (tMeta != null) res.add(tMeta);
        }

        it = equip.getBoots();
        bit = TerraBukkitAdapter.itemAdapt(it);
        if (bit instanceof TerraItem terraItem) {
            if (SlotUtil.isMainHand(terraItem.getSlotAsString()) && isValidItem(it)) {
                res.add(AttributeUtil.getMergedMeta(terraItem, it));
            }
        } else {
            tMeta = TerraBukkitAdapter.metaAdapt(it);
            if (tMeta != null) res.add(tMeta);
        }

        return res;
    }

    /**
     * 遍历目标的饰品
     */
    public static List<TerraCalculableMeta> getEffectiveAccessoryAttributePDC(LivingEntity entity) {
        return List.of();
    }

    /**
     * 通过耐久判断物品是否需要计入属性
     */
    public static boolean isValidItem(ItemStack item) {
        Integer md = PDCAPI.getMaxDamage(item);
        if (md == null) return true;
        if (md <= 0) return true;
        Integer cd = PDCAPI.getCurrentDamage(item);
        if (cd == null) return true;
        return cd > 0;
    }
}
