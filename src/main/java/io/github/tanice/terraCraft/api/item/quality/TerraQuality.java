package io.github.tanice.terraCraft.api.item.quality;

import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;

public interface TerraQuality {

    String getName();

    int getOriWeight();

    String getDisplayName();

    TerraCalculableMeta getMeta();
}
