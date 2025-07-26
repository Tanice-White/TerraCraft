package io.github.tanice.terraCraft.bukkit.events;

import io.github.tanice.terraCraft.bukkit.utils.annotation.NonnullByDefault;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@NonnullByDefault
public class TerraCraftReloadEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {return handlers;}

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
