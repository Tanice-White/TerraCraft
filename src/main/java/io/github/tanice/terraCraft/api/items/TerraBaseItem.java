package io.github.tanice.terraCraft.api.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public interface TerraBaseItem {

    List<String> selfUpdate(ItemStack old);

    String getName();

    String getDisplayName();

    String getLoreTemplateName();

    Material type();

    int getAmount();

    int getMaxStackSize();

    int getCustomModelData();

    boolean unbreakable();

    int getMaxDamage();

    String getColor();

    List<String> getHideFlags();

    Map<String, String> getCustomNBTs();

    List<String> getLore();

    ItemStack getBukkitItem();
}
