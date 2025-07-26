package io.github.tanice.terraCraft.api.items;

import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;

public interface TerraGem extends TerraBaseItem {

    double getChance();

    boolean lossWhenFailed();

    TerraCalculableMeta getMeta();
}
