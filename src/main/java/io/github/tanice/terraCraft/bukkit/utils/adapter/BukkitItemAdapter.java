package io.github.tanice.terraCraft.bukkit.utils.adapter;

import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.TerraItem;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.utils.pdc.PDCAPI;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public final class BukkitItemAdapter {

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
        Optional<TerraBaseItem> op = TerraCraftBukkit.inst().getItemManager().getItem(PDCAPI.getItemName(item));
        if (op.isEmpty()) return null;

        /* 本插件的物品计算属性 */
        TerraBaseItem baseItem = op.get();
        if (baseItem instanceof TerraItem terraItem) return terraItem.copyMeta();

        /* TODO 根据PDC判断是否为其他插件物品并创建对应的计算属性 */
        /* TODO 可以通过触发事件实现兼容 */

        /* TODO 返回原版的计算属性 */
        return null;
    }
}
