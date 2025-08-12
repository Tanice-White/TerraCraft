package io.github.tanice.terraCraft.api.items.components;

import io.github.tanice.terraCraft.api.items.TerraBaseComponent;

public interface TerraRarityComponent extends TerraBaseComponent {

    enum Rarity {
        COMMON,
        UNCOMMON,
        RARE,
        EPIC,
    }
}
