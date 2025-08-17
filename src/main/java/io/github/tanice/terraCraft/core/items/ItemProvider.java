package io.github.tanice.terraCraft.core.items;

import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.bukkit.items.*;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;

public final class ItemProvider {

    private int valid;

    public ItemProvider() {
    }

    Optional<TerraBaseItem> createItem(String name, ConfigurationSection cfg) {
        if (cfg == null) return Optional.empty();
        valid ++;
        return Optional.of(new Item(name, cfg));
    }

    public int getTotal() {
        return this.valid;
    }
}
