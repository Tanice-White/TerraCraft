package io.github.tanice.terraCraft.api.items.components;

import io.github.tanice.terraCraft.api.items.TerraBaseComponent;

public interface TerraDurabilityComponent extends TerraBaseComponent {

    int getDamage();

    void setDamage(int damage);

    int getMaxDamage();

    void setMaxDamage(int maxDamage);

    boolean isBreakLoss();

    void setBreakLoss(boolean breakLoss);
}
