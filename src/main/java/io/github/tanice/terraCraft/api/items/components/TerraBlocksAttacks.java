package io.github.tanice.terraCraft.api.items.components;

import io.github.tanice.terraCraft.api.items.TerraBaseComponent;
import io.github.tanice.terraCraft.bukkit.utils.adapter.BukkitDamageTags;

public interface TerraBlocksAttacks extends TerraBaseComponent {

    void addDamageReduction(float base, float factor, float horizontalBlockingAngle, BukkitDamageTags... types);
}
