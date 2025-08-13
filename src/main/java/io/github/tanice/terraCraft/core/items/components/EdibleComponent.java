package io.github.tanice.terraCraft.core.items.components;

import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.TerraEdibleComponent;
import io.github.tanice.terraCraft.bukkit.items.components.FoodComponent;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;

/**
 * TODO 食物的额外拓展
 */
public class EdibleComponent implements TerraEdibleComponent {

    private FoodComponent bukkitFoodComponent;

    @Override
    public void apply(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)){
            bukkitFoodComponent.apply(item);
        } else {

        }
    }
}
