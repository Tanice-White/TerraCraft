package io.github.tanice.terraCraft.bukkit.events;

import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.bukkit.utils.annotation.NonnullByDefault;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

@NonnullByDefault
public class TerraItemUpdateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final TerraBaseItem terraBaseItem;
    private final ItemStack pre;

    public TerraItemUpdateEvent(Player player, TerraBaseItem baseItem,ItemStack pre) {
        this.player = player;
        this.terraBaseItem = baseItem;
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

    public TerraBaseItem getTerraBaseItem() {
        return this.terraBaseItem;
    }

    public ItemStack getItemStack() {
        return this.pre;
    }
}
