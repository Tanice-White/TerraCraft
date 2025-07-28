package io.github.tanice.terraCraft.bukkit.events;

import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import io.github.tanice.terraCraft.bukkit.utils.annotation.NonnullByDefault;
import io.github.tanice.terraCraft.core.attribute.CalculableMeta;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

@NonnullByDefault
public class TerraCalculableMetaLoadEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final ItemStack BukkitItem;
    private final TerraCalculableMeta meta;

    public TerraCalculableMetaLoadEvent(ItemStack BukkitItem) {
        this.BukkitItem = BukkitItem;
        this.meta = new CalculableMeta();
    }

    public static HandlerList getHandlerList() {return handlers;}

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public ItemStack getBukkitItem() {
        return this.BukkitItem;
    }

    public TerraCalculableMeta getMeta() {
        return this.meta;
    }
}
