package io.github.tanice.terraCraft.core.items.qualities;

import io.github.tanice.terraCraft.api.attribute.AttributeActiveSection;
import io.github.tanice.terraCraft.api.attribute.AttributeType;
import io.github.tanice.terraCraft.api.attribute.DamageFromType;
import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import io.github.tanice.terraCraft.api.items.qualities.TerraQuality;
import io.github.tanice.terraCraft.core.attribute.CalculableMeta;
import org.bukkit.configuration.ConfigurationSection;

import static io.github.tanice.terraCraft.core.constants.ConfigKeys.*;

public class Quality implements TerraQuality {

    private final String name;
    private final int weight;
    private final String displayName;

    private final TerraCalculableMeta meta;

    public Quality(String name, int weight, String displayName, ConfigurationSection cfg) {
        this.name = name;
        this.weight = weight;
        this.displayName = displayName;
        this.meta = new CalculableMeta(cfg.getConfigurationSection(ATTRIBUTE_SECTION), AttributeActiveSection.BASE);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getOriWeight() {
        return this.weight;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public TerraCalculableMeta getMeta() {
        return this.meta.clone();
    }
}
