package io.github.tanice.terraCraft.api.items;

import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;

public interface TerraBaseComponent {

    String MINECRAFT_PREFIX = "minecraft:";
    String COMPONENT_KEY = "components";
    String TAG_KEY = "tag";

    String TERRA_PREFIX = TerraCraftBukkit.inst().getName() + ":";
    /**
     * 将组件附加到物品上
     */
    void apply(TerraBaseItem item);
}