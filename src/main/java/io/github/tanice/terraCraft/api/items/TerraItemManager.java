package io.github.tanice.terraCraft.api.items;

import io.github.tanice.terraCraft.api.items.levels.TerraLevelTemplate;
import io.github.tanice.terraCraft.api.items.qualities.TerraQualityGroup;

import java.util.Collection;
import java.util.Optional;

public interface TerraItemManager {

    Collection<String> getItemNames();

    Optional<TerraBaseItem> getItem(String name);

    boolean isTerraItem(String name);

    Collection<String> filterItems(Collection<TerraBaseItem> items, String name);

    Optional<TerraLevelTemplate> getLevelTemplate(String name);

    Optional<TerraQualityGroup> getQualityGroup(String name);
}
