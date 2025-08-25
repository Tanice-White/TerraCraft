package io.github.tanice.terraCraft.api.item.component.vanilla;

import io.github.tanice.terraCraft.api.item.component.TerraBaseComponent;

public interface TerraRarityComponent extends TerraBaseComponent {

    enum Rarity {
        COMMON,
        UNCOMMON,
        RARE,
        EPIC,
    }
}
