package io.github.tanice.terraCraft.api.items.components;

import org.bukkit.Color;

import java.util.List;

/**
 * cmd 条件组
 */
public interface TerraCustomModelDataComponent extends TerraPaperComponent {

    List<Integer> getCustomModelData();

    List<String> getStrings();

    List<Color> getColors();
}
