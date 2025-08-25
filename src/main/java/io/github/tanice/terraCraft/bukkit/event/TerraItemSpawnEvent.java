package io.github.tanice.terraCraft.bukkit.event;

import io.github.tanice.terraCraft.api.item.TerraBaseItem;
import io.github.tanice.terraCraft.bukkit.util.annotation.NonnullByDefault;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;


@NonnullByDefault
public class TerraItemSpawnEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private final TerraBaseItem terraBaseItem;
    private final ItemStack item;

    public TerraItemSpawnEvent(TerraBaseItem terraBaseItem, ItemStack item) {
        this.terraBaseItem = terraBaseItem;
        this.item = item;
    }

    public static HandlerList getHandlerList() {return handlers;}

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public TerraBaseItem getTerraBaseItem() {
        return this.terraBaseItem;
    }

    public ItemStack getItem() {
        return this.item;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}
