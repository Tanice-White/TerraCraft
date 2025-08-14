package io.github.tanice.terraCraft.bukkit.items.components;

import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.TerraEdibleComponent;
import io.github.tanice.terraCraft.bukkit.items.AbstractItemComponent;
import io.github.tanice.terraCraft.bukkit.items.components.vanilla.FoodComponent;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;

/**
 * TODO 食物的额外拓展
 */
public class EdibleComponent extends AbstractItemComponent implements TerraEdibleComponent {

    private FoodComponent bukkitFoodComponent;

    @Override
    public void apply(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)){
            bukkitFoodComponent.apply(item);
        } else {

        }
    }
}
