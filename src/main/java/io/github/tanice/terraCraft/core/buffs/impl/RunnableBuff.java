package io.github.tanice.terraCraft.core.buffs.impl;

import io.github.tanice.terraCraft.api.buffs.BuffActiveCondition;
import io.github.tanice.terraCraft.api.buffs.TerraRunnableBuff;
import io.github.tanice.terraCraft.api.attribute.AttributeActiveSection;
import io.github.tanice.terraCraft.core.buffs.AbstractBuff;

public class RunnableBuff extends AbstractBuff implements TerraRunnableBuff {

    protected String jsFileName;

    public RunnableBuff(String jsName, String name, String displayName, boolean enable, int priority, double chance, int duration, BuffActiveCondition bac, AttributeActiveSection aas) {
        super(name, displayName, enable, priority, chance, duration, bac, aas);
        this.jsFileName = jsName;
    }

    @Override
    public String getFileName() {
        return this.jsFileName;
    }

    @Override
    public RunnableBuff clone() {
        RunnableBuff clone = (RunnableBuff) super.clone();
        clone.jsFileName = this.jsFileName;
        return clone;
    }
}
