package io.github.tanice.terraCraft.core.buffs.impl;

import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import io.github.tanice.terraCraft.api.buffs.BuffActiveCondition;
import io.github.tanice.terraCraft.api.buffs.TerraBuff;
import io.github.tanice.terraCraft.api.attribute.AttributeActiveSection;
import io.github.tanice.terraCraft.core.attribute.CalculableMeta;
import io.github.tanice.terraCraft.core.buffs.AbstractBuff;
import org.bukkit.configuration.ConfigurationSection;

import static io.github.tanice.terraCraft.core.constants.ConfigKeys.ATTRIBUTE_SECTION;

public class AttributeBuff extends AbstractBuff implements TerraBuff {

    private TerraCalculableMeta meta;

    public AttributeBuff(String name, ConfigurationSection cfg, AttributeActiveSection aas, BuffActiveCondition bac) {
        super(name, cfg, bac, aas);
        meta = new CalculableMeta(cfg.getConfigurationSection(ATTRIBUTE_SECTION), aas);
    }

    public TerraCalculableMeta getMeta() {
        return this.meta.clone();
    }

    @Override
    public AttributeBuff clone() {
        AttributeBuff clone = (AttributeBuff) super.clone();
        clone.meta = meta.clone();
        return clone;
    }
}
