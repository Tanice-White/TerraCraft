package io.github.tanice.terraCraft.core.buff.impl;

import io.github.tanice.terraCraft.api.buff.BuffActiveCondition;
import io.github.tanice.terraCraft.api.buff.TerraTimerBuff;
import io.github.tanice.terraCraft.api.attribute.AttributeActiveSection;

import java.util.Collection;
import java.util.Set;

import static io.github.tanice.terraCraft.api.command.TerraCommand.AQUA;
import static io.github.tanice.terraCraft.api.command.TerraCommand.WHITE;

public class TimerBuff extends RunnableBuff implements TerraTimerBuff {
    /** 激活间隔(tick) */
    private int cd;

    public TimerBuff(String jsName, String name, String displayName, boolean enable, int priority, double chance, int duration, Set<String> mutex, Collection<String> override, BuffActiveCondition bac, AttributeActiveSection aas, int cd) {
        super(jsName, name, displayName, enable, priority, chance, duration, mutex, override, bac, aas);
        this.cd = cd;
    }

    @Override
    public int getCd() {
        return this.cd;
    }

    @Override
    public void setCd(int cd) {
        this.cd = cd;
    }

    @Override
    public TimerBuff clone() {
        TimerBuff clone = (TimerBuff) super.clone();
        clone.cd = this.cd;
        return clone;
    }

    @Override
    public String toString() {
        return super.toString() + "\n" + AQUA + "cd:" + WHITE + cd;
    }
}
