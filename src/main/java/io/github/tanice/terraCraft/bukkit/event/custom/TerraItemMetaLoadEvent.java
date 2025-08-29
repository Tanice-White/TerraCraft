package io.github.tanice.terraCraft.bukkit.event.custom;

import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import io.github.tanice.terraCraft.bukkit.util.annotation.NonnullByDefault;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

@NonnullByDefault
public class TerraItemMetaLoadEvent extends Event {
    protected static final HandlerList handlers = new HandlerList();

    @Nullable
    protected TerraCalculableMeta meta;
    private final ItemStack BukkitItem;

    public TerraItemMetaLoadEvent(ItemStack item) {
        this.meta = null;
        this.BukkitItem = item;
    }
    public static HandlerList getHandlerList() {return handlers;}

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Nullable
    public TerraCalculableMeta getMeta() {
        return this.meta;
    }

    public void setMeta(TerraCalculableMeta meta) {
        this.meta = meta;
    }

    public ItemStack getBukkitItem() {
        return this.BukkitItem;
    }
}
