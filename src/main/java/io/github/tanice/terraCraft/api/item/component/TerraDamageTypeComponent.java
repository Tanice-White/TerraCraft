package io.github.tanice.terraCraft.api.item.component;

import io.github.tanice.terraCraft.api.attribute.DamageFromType;

public interface TerraDamageTypeComponent extends TerraBaseComponent {

    DamageFromType getType();

    void setType(DamageFromType type);
}
