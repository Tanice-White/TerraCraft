package io.github.tanice.terraCraft.bukkit.events.load;

import io.github.tanice.terraCraft.bukkit.events.AbstractTerraMetaLoadEvent;
import io.github.tanice.terraCraft.bukkit.utils.annotation.NonnullByDefault;
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
