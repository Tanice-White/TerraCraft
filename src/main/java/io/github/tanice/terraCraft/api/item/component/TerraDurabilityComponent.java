package io.github.tanice.terraCraft.api.item.component;

public interface TerraDurabilityComponent extends TerraBaseComponent {

    int getDamage();

    void setDamage(int damage);

    int getMaxDamage();

    void setMaxDamage(int maxDamage);

    boolean isBreakLoss();

    void setBreakLoss(boolean breakLoss);

    boolean broken();
}
