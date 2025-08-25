package io.github.tanice.terraCraft.api.item.component.vanilla;

import io.github.tanice.terraCraft.api.item.component.TerraBaseComponent;

public interface TerraConsumableComponent extends TerraBaseComponent {

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
