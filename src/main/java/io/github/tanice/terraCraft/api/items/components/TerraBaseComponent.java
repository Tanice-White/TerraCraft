package io.github.tanice.terraCraft.api.items.components;

import org.bukkit.inventory.ItemStack;

public interface TerraBaseComponent {

    String MINECRAFT_PREFIX = "minecraft:";
    String TERRA_COMPONENT_KEY = "terracraft:components";
    /**
     * 将组件附加到物品上
     */
    void apply(ItemStack item);

    /**
     * 组件是否可更新
     */
    default boolean canUpdate() {
        return true;
    }

    /**
     * 更新时继承部分值
     */
    default TerraBaseComponent updatePartial() {
        /* 默认不需要继承任何值 */
        return this;
    }
}