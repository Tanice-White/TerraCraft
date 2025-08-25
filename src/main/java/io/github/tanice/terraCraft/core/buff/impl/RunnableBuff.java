package io.github.tanice.terraCraft.core.buff.impl;

import io.github.tanice.terraCraft.api.buff.BuffActiveCondition;
import io.github.tanice.terraCraft.api.buff.TerraRunnableBuff;
import io.github.tanice.terraCraft.api.attribute.AttributeActiveSection;
import io.github.tanice.terraCraft.core.buff.AbstractBuff;

import java.util.Collection;
import java.util.Set;

import static io.github.tanice.terraCraft.api.command.TerraCommand.AQUA;
import static io.github.tanice.terraCraft.api.command.TerraCommand.WHITE;

public class RunnableBuff extends AbstractBuff implements TerraRunnableBuff {

    protected String jsFileName;

    public RunnableBuff(String jsName, String name, String displayName, boolean enable, int priority, double chance, int duration, Set<String> mutex, Collection<String> override, BuffActiveCondition bac, AttributeActiveSection aas) {
        super(name, displayName, enable, priority, chance, duration, mutex, override, bac, aas);
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

    @Override
    public String toString() {
        return super.toString() + "\n" + AQUA + "Js file name:" + WHITE + jsFileName;
    }
}
