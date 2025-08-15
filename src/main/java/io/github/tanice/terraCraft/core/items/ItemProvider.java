package io.github.tanice.terraCraft.core.items;

import io.github.tanice.terraCraft.api.attribute.AttributeActiveSection;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.bukkit.items.*;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;

import static io.github.tanice.terraCraft.core.constants.ConfigKeys.ACTIVE_SECTION;
import static io.github.tanice.terraCraft.core.utils.EnumUtil.safeValueOf;

public final class ItemProvider {

    private int valid;
    private int other;

    public ItemProvider() {

    }

    Optional<TerraBaseItem> createItem(String name, ConfigurationSection cfg) {
        if (cfg == null) return Optional.empty();
        AttributeActiveSection aas = safeValueOf(AttributeActiveSection.class, cfg.getString(ACTIVE_SECTION), AttributeActiveSection.ERROR);
        if (checkAttributeActiveSection(name, aas)) return Optional.of(new Item(name, cfg, aas));
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
}
