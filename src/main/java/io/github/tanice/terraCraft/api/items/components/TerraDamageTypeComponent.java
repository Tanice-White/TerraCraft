package io.github.tanice.terraCraft.api.items.components;

import io.github.tanice.terraCraft.api.attribute.DamageFromType;

public interface TerraDamageTypeComponent extends TerraBaseComponent {

    DamageFromType getType();

    void setType(DamageFromType type);
}
