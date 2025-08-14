package io.github.tanice.terraCraft.bukkit.items;

import io.github.tanice.terraCraft.api.items.TerraBaseComponent;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.ComponentState;

import javax.annotation.Nullable;

public abstract class AbstractItemComponent implements TerraBaseComponent {

    protected ComponentState state;

    public AbstractItemComponent(boolean updatable) {
        state = new ComponentState(true, false, updatable);
    }

    public AbstractItemComponent(ComponentState state) {
        this.state = state;
    }

    public abstract void apply(TerraBaseItem item);

    @Override
    public @Nullable ComponentState getState() {
        return this.state;
    }

    @Override
    public void setState(@Nullable ComponentState state) {
        if (state == null) this.state = new ComponentState(null, null, null);
        else this.state = state;
    }
}
