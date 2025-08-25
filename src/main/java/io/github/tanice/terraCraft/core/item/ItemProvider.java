package io.github.tanice.terraCraft.core.item;

import io.github.tanice.terraCraft.api.item.TerraBaseItem;
import io.github.tanice.terraCraft.bukkit.item.*;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;

public final class ItemProvider {

    private int valid;

    public ItemProvider() {
        valid = 0;
    }

    Optional<TerraBaseItem> createItem(String name, ConfigurationSection cfg) {
        if (cfg == null) return Optional.empty();
        valid ++;
        return Optional.of(new Item(name, cfg));
    }

    public int getTotal() {
        return this.valid;
    }

    public void reload() {
        valid = 0;
    }
}
