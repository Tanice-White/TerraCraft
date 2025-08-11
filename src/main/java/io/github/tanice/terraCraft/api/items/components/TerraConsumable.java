package io.github.tanice.terraCraft.api.items.components;

import io.github.tanice.terraCraft.api.items.TerraBaseComponent;

public interface TerraConsumable extends TerraBaseComponent {

    enum Animation {
        NONE,
        EAT,
        DRINK,
        BLOCK,
        BOW,
        BRUSH,
        CROSSBOW,
        SPEAR,
        SPYGLASS,
        TOOT_HORN,
        /** 1.21.4 之后 */
        BUNDLE,
    }
}
