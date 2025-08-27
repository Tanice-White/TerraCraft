package io.github.tanice.terraCraft.core.item.quality;

import io.github.tanice.terraCraft.api.attribute.AttributeActiveSection;
import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import io.github.tanice.terraCraft.api.item.quality.TerraQuality;
import io.github.tanice.terraCraft.core.attribute.CalculableMeta;
import org.bukkit.configuration.ConfigurationSection;

import static io.github.tanice.terraCraft.api.command.TerraCommand.*;
import static io.github.tanice.terraCraft.core.util.EnumUtil.safeValueOf;

public class Quality implements TerraQuality {

    private final String name;
    private final int weight;
    private final String displayName;

    private final TerraCalculableMeta meta;

    public Quality(String name, int weight, String displayName, ConfigurationSection cfg) {
        this.name = name;
        this.weight = weight;
        this.displayName = displayName;
        this.meta = new CalculableMeta(cfg.getConfigurationSection("attribute"), safeValueOf(AttributeActiveSection.class, cfg.getString("section"), AttributeActiveSection.BASE));
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

    @Override
    public String toString() {
        return BOLD + YELLOW + "Quality:" + "\n" +
                "    " + AQUA + "name:" + WHITE + name + "\n" +
                "    " + AQUA + "displayName:" + WHITE + displayName + "\n" +
                "    " + AQUA + "weight:" + WHITE + weight + RESET + "\n" +
                meta.toString() + RESET;
    }
}
