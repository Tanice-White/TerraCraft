package io.github.tanice.terraCraft.bukkit.event;

import io.github.tanice.terraCraft.bukkit.util.annotation.NonnullByDefault;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

@NonnullByDefault
public class TerraItemUpdateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final ItemStack pre;

    public TerraItemUpdateEvent(Player player, ItemStack pre) {
        this.player = player;
        this.pre = pre;
    }

    public static HandlerList getHandlerList() {return handlers;}

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return this.player;
    }

    public ItemStack getItemStack() {
        return this.pre;
    }
}
