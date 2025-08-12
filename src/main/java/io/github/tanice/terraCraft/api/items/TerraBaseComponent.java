package io.github.tanice.terraCraft.api.items;

public interface TerraBaseComponent {

    String MINECRAFT_PREFIX = "minecraft:";

    String COMPONENT_KEY = "components";
    String TAG_KEY = "tag";
    /**
     * 将组件附加到物品上
     *
     * @param item bukkit物品
     */
    void apply(TerraBaseItem item);
}