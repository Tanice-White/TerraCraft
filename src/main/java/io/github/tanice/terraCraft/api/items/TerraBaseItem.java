package io.github.tanice.terraCraft.api.items;

import io.github.tanice.terraCraft.api.items.components.TerraBaseComponent;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public interface TerraBaseItem {

    Set<TerraBaseComponent> getVanillaComponents();

    ItemStack getBukkitItem();

    String getName();

    void selfUpdate(ItemStack old);

    int getHashCode();
}
