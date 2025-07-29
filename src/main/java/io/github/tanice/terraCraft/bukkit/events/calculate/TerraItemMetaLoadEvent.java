package io.github.tanice.terraCraft.bukkit.events.calculate;

import io.github.tanice.terraCraft.bukkit.utils.annotation.NonnullByDefault;
import org.bukkit.inventory.ItemStack;

@NonnullByDefault
public class TerraItemMetaLoadEvent extends AbstractTerraMetaLoadEvent {
    private final ItemStack BukkitItem;

    public TerraItemMetaLoadEvent(ItemStack BukkitItem) {
        super();
        this.BukkitItem = BukkitItem;
    }

    public ItemStack getBukkitItem() {
        return this.BukkitItem;
    }
}
