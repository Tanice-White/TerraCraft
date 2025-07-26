package io.github.tanice.terraCraft.core.items;

import io.github.tanice.terraCraft.api.attribute.AttributeActiveSection;
import io.github.tanice.terraCraft.api.items.ItemType;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.bukkit.items.*;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;

import static io.github.tanice.terraCraft.core.constants.ConfigKeys.ACTIVE_SECTION;
import static io.github.tanice.terraCraft.core.constants.ConfigKeys.ITEM_TYPE;
import static io.github.tanice.terraCraft.core.utils.EnumUtil.safeValueOf;

public final class ItemProvider {

    private int valid;
    private int other;

    public ItemProvider() {

    }

    Optional<TerraBaseItem> createItem(String name, ConfigurationSection cfg) {
        if (cfg == null) return Optional.empty();

        ItemType type = safeValueOf(ItemType.class, cfg.getString(ITEM_TYPE), ItemType.OTHER);
        AttributeActiveSection aas = safeValueOf(AttributeActiveSection.class, cfg.getString(ACTIVE_SECTION), AttributeActiveSection.ERROR);
        if (checkItemType(name, type)) {
            switch (type) {
                case MATERIAL -> {
                    return Optional.of(new Material(name, cfg));
                }
                case EDIBLE -> {
                    return Optional.of(new Edible(name, cfg));
                }
                case GEM -> {
                    if (checkAttributeActiveSection(name, aas)) return Optional.of(new Gem(name, cfg, aas));
                    else return Optional.empty();
                }
                case TOOL -> {
                    return Optional.of(new Tool(name, cfg));
                }
                case ITEM -> {
                    if (checkAttributeActiveSection(name, aas)) return Optional.of(new Item(name, cfg, aas));
                    else return Optional.empty();
                }
            }
        }
        return Optional.empty();
    }

    public int getTotal() {
        return this.valid + this.other;
    }

    public int getValid() {
        return this.valid;
    }

    public int getOther() {
        return this.other;
    }

    private boolean checkAttributeActiveSection(String name, AttributeActiveSection aas) {
        if (aas == AttributeActiveSection.ERROR) {
            TerraCraftLogger.warning("item: " + name + " AttributeActiveSection read failure (OTHER as default). This section will be unavailable for calculation");
            other ++;
            return false;
        }
        valid ++;
        return true;
    }

    private boolean checkItemType(String name, ItemType type) {
        if (type == ItemType.OTHER) {
            TerraCraftLogger.warning("item: " + name + " type read failure (OTHER as default). This section will be unavailable for generation");
            other ++;
            return false;
        }
        valid ++;
        return true;
    }
}
