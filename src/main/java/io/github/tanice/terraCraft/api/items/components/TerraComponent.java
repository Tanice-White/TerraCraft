package io.github.tanice.terraCraft.api.items.components;

import org.bukkit.inventory.meta.ItemMeta;

public interface TerraComponent {
    /**
     * 将组件附加到物品上
     * @param meta 物品meta
     */
    void apply(ItemMeta meta);
}