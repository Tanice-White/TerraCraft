package io.github.tanice.terraCraft.api.items.level;

import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;

public interface TerraLevel {

    String getName();

    int getBegin();

    int getMax();

    double getChance();

    String getMaterial();

    boolean isFailedLevelDown();

    TerraCalculableMeta getMeta();
}
