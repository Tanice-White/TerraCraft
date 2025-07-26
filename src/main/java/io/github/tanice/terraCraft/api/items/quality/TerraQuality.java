package io.github.tanice.terraCraft.api.items.quality;

import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import io.github.tanice.terraCraft.core.items.quality.Operation;

public interface TerraQuality {

    void apply(TerraCalculableMeta other);

    String getName();

    int getOriWeight();

    String getDisplayName();

    Operation getOp();

    TerraCalculableMeta getMeta();
}
