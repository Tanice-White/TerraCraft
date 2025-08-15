package io.github.tanice.terraCraft.api.items.components;

import io.github.tanice.terraCraft.api.items.TerraBaseItem;

public interface TerraBaseComponent {

    String MINECRAFT_PREFIX = "minecraft:";
    String COMPONENT_KEY = "components";
    String TAG_KEY = "tag";
    String TERRA_COMPONENT_KEY = "terracraft:components";
    /**
     * 将组件附加到物品上
     */
    void apply(TerraBaseItem item);

    /**
     * 组件是否可更新
     */
    default boolean canUpdate() {
        return true;
    }

    /**
     * 更新时继承部分值
     */
    default void updatePartialFrom(TerraBaseComponent old) {
        /* 默认不需要继承任何值 */
    }
}