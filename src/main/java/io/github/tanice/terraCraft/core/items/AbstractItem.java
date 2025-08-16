package io.github.tanice.terraCraft.core.items;

import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.TerraBaseComponent;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static io.github.tanice.terraCraft.core.utils.EnumUtil.safeValueOf;

public abstract class AbstractItem implements TerraBaseItem {
    private final Material material;
    private final int amount;
    private final List<TerraBaseComponent> components;
    private final ItemStack bukkitItem;

    public AbstractItem(ConfigurationSection cfg) {
        Objects.requireNonNull(cfg, "Item configurationSection cannot be null");
        this.material = safeValueOf(Material.class, cfg.getString("id"), Material.STONE);
        this.amount = Math.max(cfg.getInt("amount"), 1);
        this.components = new ArrayList<>();
        this.bukkitItem = new ItemStack(material, amount);


    }
}
