package io.github.tanice.terraCraft.bukkit.items;

import io.github.tanice.terraCraft.api.items.TerraMaterial;
import io.github.tanice.terraCraft.bukkit.utils.annotation.NonnullByDefault;
import io.github.tanice.terraCraft.core.items.AbstractItem;
import org.bukkit.configuration.ConfigurationSection;

/**
 * 普通的材料
 */
@NonnullByDefault
public class Material extends AbstractItem implements TerraMaterial {

    public Material(String name, ConfigurationSection cfg) {
        super(name, cfg);
    }
}
