package io.github.tanice.terraCraft.api.items.components;

import io.github.tanice.terraCraft.api.items.TerraBaseComponent;
import io.github.tanice.terraCraft.bukkit.utils.nbtapi.NBTEffect;

public interface TerraDeathProtectionComponent extends TerraBaseComponent {

    void addEffect(NBTEffect effect);
}
