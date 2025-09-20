package io.github.tanice.terraCraft.bukkit.event.custom;

import io.github.tanice.terraCraft.bukkit.util.annotation.NonnullByDefault;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

@NonnullByDefault
public class TerraItemMetaLoadEvent extends AbstractTerraMetaLoadEvent {
    private final ItemStack bukkitItem;

    public TerraItemMetaLoadEvent(ItemStack item) {
        super();
        this.bukkitItem = item;
    }

    public ItemStack getBukkitItem() {
        return this.bukkitItem;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
