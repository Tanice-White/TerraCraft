package io.github.tanice.terraCraft.bukkit.event;

import io.github.tanice.terraCraft.bukkit.util.annotation.NonnullByDefault;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@NonnullByDefault
public abstract class AbstractTerraEvent extends Event implements Cancellable {
    protected static final HandlerList handlers = new HandlerList();
    protected boolean cancelled;

    protected final LivingEntity entity;

    public AbstractTerraEvent(final LivingEntity entity) {
        this.entity = entity;
    }

    public static HandlerList getHandlerList() {return handlers;}

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    public LivingEntity getEntity() {
        return this.entity;
    }
}
