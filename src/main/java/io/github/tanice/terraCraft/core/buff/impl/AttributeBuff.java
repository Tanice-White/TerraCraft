package io.github.tanice.terraCraft.core.buff.impl;

import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import io.github.tanice.terraCraft.api.buff.BuffActiveCondition;
import io.github.tanice.terraCraft.api.buff.TerraBuff;
import io.github.tanice.terraCraft.api.attribute.AttributeActiveSection;
import io.github.tanice.terraCraft.bukkit.item.component.MetaComponent;
import io.github.tanice.terraCraft.core.buff.AbstractBuff;
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

    @Override
    public String toString() {
        return super.toString() + "\n" + meta.toString();
    }
}
