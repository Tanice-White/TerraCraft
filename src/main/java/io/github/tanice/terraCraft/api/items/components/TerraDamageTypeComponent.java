package io.github.tanice.terraCraft.api.items.components;

import io.github.tanice.terraCraft.api.attribute.DamageFromType;
import io.github.tanice.terraCraft.api.items.TerraBaseComponent;

public interface TerraDamageTypeComponent extends TerraBaseComponent {

    DamageFromType getType();

    void setType(DamageFromType type);
}
