package io.github.tanice.terraCraft.bukkit.event;

import io.github.tanice.terraCraft.bukkit.util.annotation.NonnullByDefault;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@NonnullByDefault
public abstract class AbstractTerraEvent extends Event {
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

    public LivingEntity getEntity() {
        return this.entity;
    }
}
