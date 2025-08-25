package io.github.tanice.terraCraft.api.item;

import io.github.tanice.terraCraft.api.item.component.TerraBaseComponent;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public interface TerraBaseItem {

    Set<TerraBaseComponent> getVanillaComponents();

    ItemStack getBukkitItem();

    String getName();

    void updateOld(ItemStack old);

    int getHashCode();
}
