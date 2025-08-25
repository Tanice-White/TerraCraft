package io.github.tanice.terraCraft.api.item;

import io.github.tanice.terraCraft.api.item.level.TerraLevelTemplate;
import io.github.tanice.terraCraft.api.item.quality.TerraQualityGroup;

import java.util.Collection;
import java.util.Optional;

public interface TerraItemManager {

    Collection<String> getItemNames();

    Optional<TerraBaseItem> getItem(String name);

    boolean isTerraItem(String name);

    Collection<String> filterItems(String name);

    Optional<TerraLevelTemplate> getLevelTemplate(String name);

    Optional<TerraQualityGroup> getQualityGroup(String name);
}
