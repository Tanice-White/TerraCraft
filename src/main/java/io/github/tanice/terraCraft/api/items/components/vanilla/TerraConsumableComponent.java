package io.github.tanice.terraCraft.api.items.components.vanilla;

import io.github.tanice.terraCraft.api.items.components.TerraBaseComponent;
import io.github.tanice.terraCraft.bukkit.utils.nbtapi.NBTEffect;

public interface TerraConsumableComponent extends TerraBaseComponent {

    void addEffect(NBTEffect effect);

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
