package io.github.tanice.terraCraft.bukkit.utils.adapter;

import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.utils.pdc.PDCAPI;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public final class BukkitItemAdapter {

    public static TerraBaseItem adapt(ItemStack item) {
        if (item == null) return null;
        Optional<TerraBaseItem> baseItem = TerraCraftBukkit.inst().getItemManager().getItem(PDCAPI.getItemName(item));
        return baseItem.orElse(null);
    }
}
