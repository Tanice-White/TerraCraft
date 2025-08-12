package io.github.tanice.terraCraft.api.items.components;

import io.github.tanice.terraCraft.api.items.TerraBaseComponent;
import io.github.tanice.terraCraft.core.utils.namespace.TerraNamespaceKey;

public interface TerraBlocksAttacksComponent extends TerraBaseComponent {

    void addDamageReduction(float base, float factor, float horizontalBlockingAngle, TerraNamespaceKey... types);
}
