package io.github.tanice.terraCraft.api.items.components;

import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;

public interface TerraMetaComponent extends TerraBaseComponent {

    TerraCalculableMeta getMeta();

    void setMeta(TerraCalculableMeta meta);
}
