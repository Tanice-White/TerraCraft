package io.github.tanice.terraCraft.bukkit.event.custom;

import io.github.tanice.terraCraft.bukkit.event.AbstractTerraMetaLoadEvent;
import io.github.tanice.terraCraft.bukkit.util.annotation.NonnullByDefault;
import org.bukkit.inventory.ItemStack;

@NonnullByDefault
public class TerraItemMetaLoadEvent extends AbstractTerraMetaLoadEvent {
    private final ItemStack BukkitItem;

    public TerraItemMetaLoadEvent(ItemStack item) {
        super();
        this.BukkitItem = item;
    }

    public ItemStack getBukkitItem() {
        return this.BukkitItem;
    }
}
