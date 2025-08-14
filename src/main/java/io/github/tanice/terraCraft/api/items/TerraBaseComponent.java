package io.github.tanice.terraCraft.api.items;

import io.github.tanice.terraCraft.api.items.components.ComponentState;

import javax.annotation.Nullable;

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
     * 清除本组件内容
     */
    void clear(TerraBaseItem item);

    /**
     * 禁用本组件
     */
    void remove(TerraBaseItem item);

    /**
     * 组件更新
     */
    void updateBy(TerraBaseComponent newer);

    /**
     * 只有非原版NBT组件可以获取
     */
    default @Nullable ComponentState getState() {
        return null;
    }
    /**
     * 只有非原版NBT组件可以设置
     */
    default void setState(@Nullable ComponentState state) {
    }
}