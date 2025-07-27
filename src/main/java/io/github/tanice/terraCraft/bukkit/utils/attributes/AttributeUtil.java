package io.github.tanice.terraCraft.bukkit.utils.attributes;

import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import io.github.tanice.terraCraft.api.items.TerraGem;
import io.github.tanice.terraCraft.api.items.TerraItem;
import io.github.tanice.terraCraft.api.items.TerraItemManager;
import io.github.tanice.terraCraft.api.items.levels.TerraLeveled;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.utils.pdc.PDCAPI;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import org.bukkit.inventory.ItemStack;

public final class AttributeUtil {
    /***
     * 合并物品自身meta（副本）以及gemMeta、levelMeta
     * @param terraItem 插件物品基类
     * @param item bukkit实例物品
     * @return 合并完成的 meta
     */
    public static TerraCalculableMeta getMergedMeta(TerraItem terraItem, ItemStack item) {
        TerraItemManager itemManager = TerraCraftBukkit.inst().getItemManager();
        TerraCalculableMeta meta = terraItem.copyMeta();
        if (terraItem instanceof TerraLeveled terraLeveled) {
            final ItemStack fit = item;
            itemManager.getLevelTemplate(terraLeveled.getLevelTemplateName()).ifPresent(template -> {
                int v = PDCAPI.getLevel(fit);
                /* 自动修正不提示 */
                if (v < template.getBegin()) v = template.getBegin();
                if (v > template.getMax()) v = template.getMax();
                meta.add(template.getMeta(), v);
            });
        }

        String[] gems = PDCAPI.getGems(item);
        if (gems != null) {
            for (String gn : gems) {
                itemManager.getItem(gn).ifPresent(bg ->{
                    if (bg instanceof TerraGem g) meta.add(g.getMeta(), 1);
                    else TerraCraftLogger.warning("Item '" + gn + "' is not a TerraGem, actual type: " + bg.getClass().getSimpleName());
                });
            }
        }
        return meta;
    }
}
