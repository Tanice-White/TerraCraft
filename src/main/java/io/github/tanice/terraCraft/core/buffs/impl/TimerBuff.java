package io.github.tanice.terraCraft.core.buffs.impl;

import io.github.tanice.terraCraft.api.buffs.BuffActiveCondition;
import io.github.tanice.terraCraft.api.buffs.TerraTimerBuff;
import io.github.tanice.terraCraft.api.attribute.AttributeActiveSection;

public class TimerBuff extends RunnableBuff implements TerraTimerBuff {
    /** 激活间隔(tick) */
    private int cd;

    public TimerBuff(String jsName, String name, String displayName, boolean enable, int priority, double chance, int duration, BuffActiveCondition bac, AttributeActiveSection aas, int cd) {
        super(jsName, name, displayName, enable, priority, chance, duration, bac, aas);
        this.cd = cd;
    }

    @Override
    public int getCd() {
        return this.cd;
    }

    @Override
    public TimerBuff clone() {
        TimerBuff clone = (TimerBuff) super.clone();
        clone.cd = this.cd;
        return clone;
    }
}
