package io.github.tanice.terraCraft.api.items.components;

import org.bukkit.inventory.ItemStack;

public interface TerraPaperComponent {
    /**
     * 将组件附加到物品上
     *
     * @param item bukkit物品
     */
    void apply(ItemStack item);
}