package io.github.tanice.terraCraft.core.buffs.impl;

import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import io.github.tanice.terraCraft.api.buffs.BuffActiveCondition;
import io.github.tanice.terraCraft.api.buffs.TerraBuff;
import io.github.tanice.terraCraft.api.attribute.AttributeActiveSection;
import io.github.tanice.terraCraft.bukkit.items.components.MetaComponent;
import io.github.tanice.terraCraft.core.buffs.AbstractBuff;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Set;

public class AttributeBuff extends AbstractBuff implements TerraBuff {

    private TerraCalculableMeta meta;

    public AttributeBuff(String name, ConfigurationSection cfg, Set<String> mutex, AttributeActiveSection aas, BuffActiveCondition bac) {
        super(name, cfg, mutex, bac, aas);
        meta = new MetaComponent(cfg).getMeta();
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
