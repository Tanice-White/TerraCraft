package io.github.tanice.terraCraft.api.items.components;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public abstract class AbstractItemComponent implements TerraBaseComponent {

    protected ComponentState state;

    public AbstractItemComponent(boolean updatable) {
        state = new ComponentState(true, false, updatable);
    }

    public AbstractItemComponent(ComponentState state) {
        this.state = state;
    }

    public ComponentState getState() {
        return this.state;
    }

    public void setState(@Nullable ComponentState state) {
        if (state == null) this.state = new ComponentState(null, null, null);
        else this.state = state;
    }

    @Override
    public boolean canUpdate() {
        return state.isUpdatable() && state.isOriginal();
    }

    @Override
    public void apply(ItemStack item) {
        doApply(item);
        updateLore();
        callEvent();
    }

    public abstract void doApply(ItemStack item);

    public abstract void callEvent();

    public abstract void updateLore();
}
