package io.github.tanice.terraCraft.api.items;

import io.github.tanice.terraCraft.api.items.components.TerraBaseComponent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

// TODO Component化
public interface TerraBaseItem {

    Set<TerraBaseComponent> getVanillaComponents();

    ItemStack getBukkitItem();

    List<String> selfUpdate(ItemStack old);

    String getName();

    int getHashCode();
}
