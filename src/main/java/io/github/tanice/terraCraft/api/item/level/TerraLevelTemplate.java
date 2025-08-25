package io.github.tanice.terraCraft.api.item.level;

import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;

public interface TerraLevelTemplate {

    String getName();

    int getBegin();

    int getMax();

    double getChance();

    String getMaterial();

    boolean isFailedLevelDown();

    TerraCalculableMeta getMeta();
}
