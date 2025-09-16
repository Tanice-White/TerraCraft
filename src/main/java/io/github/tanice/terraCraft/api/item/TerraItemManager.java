package io.github.tanice.terraCraft.api.item;

import io.github.tanice.terraCraft.api.item.level.TerraLevelTemplate;
import io.github.tanice.terraCraft.api.item.quality.TerraQuality;
import io.github.tanice.terraCraft.api.item.quality.TerraQualityGroup;

import java.util.Collection;
import java.util.Optional;

public interface TerraItemManager {

    Collection<String> getItemNames();

    Optional<TerraBaseItem> getItem(String name);

    boolean isTerraItem(String name);

    Collection<String> filterItems(String name);

    Collection<String> filterTemplates(String name);

    Collection<String> filterQualityGroups(String name);

    Collection<String> filterQualities(String name);

    Optional<TerraLevelTemplate> getLevelTemplate(String name);

    Optional<TerraQualityGroup> getQualityGroup(String name);

    Optional<TerraQuality> getQuality(String name);
}
