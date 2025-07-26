package io.github.tanice.terraCraft.bukkit.events.entity;

import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.bukkit.events.AbstractTerraEvent;
import io.github.tanice.terraCraft.bukkit.utils.annotation.NonnullByDefault;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;


@NonnullByDefault
public class TerraItemSpawnEvent extends AbstractTerraEvent {

    private final TerraBaseItem terraBaseItem;
    private final ItemStack item;

    public TerraItemSpawnEvent(Player player, TerraBaseItem terraBaseItem, ItemStack item) {
        super(player);
        this.terraBaseItem = terraBaseItem;
        this.item = item;
    }

    public Player getEntity() {
        return (Player) this.entity;
    }

    public TerraBaseItem getTerraBaseItem() {
        return this.terraBaseItem;
    }

    public ItemStack getItem() {
        return this.item;
    }
}
