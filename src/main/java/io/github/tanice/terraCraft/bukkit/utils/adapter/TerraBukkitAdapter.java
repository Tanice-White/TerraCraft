package io.github.tanice.terraCraft.bukkit.utils.adapter;

import io.github.tanice.terraCraft.api.attribute.AttributeActiveSection;
import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.TerraItem;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.events.calculate.TerraEnchantMetaLoadEvent;
import io.github.tanice.terraCraft.bukkit.events.calculate.TerraItemMetaLoadEvent;
import io.github.tanice.terraCraft.bukkit.utils.events.TerraEvents;
import io.github.tanice.terraCraft.bukkit.utils.pdc.PDCAPI;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public final class TerraBukkitAdapter {

    /**
     * 通过Bukkit物品获取插件物品实例
     * @param item Bukkit物品
     * @return 插件物品实例
     */
    public static TerraBaseItem itemAdapt(ItemStack item) {
        if (item == null) return null;
        Optional<TerraBaseItem> baseItem = TerraCraftBukkit.inst().getItemManager().getItem(PDCAPI.getItemName(item));
        return baseItem.orElse(null);
    }

    /**
     * 通过Bukkit物品获取插件计算属性
     * @param item Bukkit物品
     * @return 插件计算属性
     */
    public static TerraCalculableMeta metaAdapt(ItemStack item) {
        if (item == null) return null;
        return TerraCraftBukkit.inst().getItemManager().getItem(PDCAPI.getItemName(item))
                .map(baseItem -> {
                    /* 本插件的物品计算属性 */
                    if (baseItem instanceof TerraItem ti) return ti.copyMeta();
                    return null;
                })
                .orElseGet(() ->{
                    /* 是否为其他插件物品并创建对应的计算属性 */
                    TerraItemMetaLoadEvent event = TerraEvents.callAndReturn(new TerraItemMetaLoadEvent(item));
                    if (event.getMeta().getActiveSection() != AttributeActiveSection.INNER) return event.getMeta();
                    /* TODO 返回原版属性 */
                    return null;
                });
    }

    /**
     * 将原版附魔转变为计算属性
     * @param enchantName 附魔的名称
     * @return 计算属性
     */
    public static TerraCalculableMeta metaAdapt(String enchantName) {
        if (enchantName == null) return null;
        TerraEnchantMetaLoadEvent event = TerraEvents.callAndReturn(new TerraEnchantMetaLoadEvent(enchantName));
        if (event.getMeta().getActiveSection() != AttributeActiveSection.INNER) return event.getMeta();
        /* TODO 查询原版属性 */
        return null;
    }
}
