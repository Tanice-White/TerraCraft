package io.github.tanice.terraCraft.core.buffs;

import io.github.tanice.terraCraft.api.buffs.BuffActiveCondition;
import io.github.tanice.terraCraft.api.buffs.TerraBaseBuff;
import io.github.tanice.terraCraft.api.attribute.AttributeActiveSection;
import org.bukkit.configuration.ConfigurationSection;

import static io.github.tanice.terraCraft.core.constants.ConfigKeys.*;

/**
 * BUFF 属性抽象
 */
public abstract class AbstractBuff implements TerraBaseBuff, Cloneable {
    /** buff 名称 */
    protected String name;
    /** buff 显示名称 */
    protected String displayName;
    /** buff 是否启用 */
    protected boolean enable;
    /** 优先级 */
    protected int priority;
    /** 激活几率 */
    protected double chance;
    /** 持续时间 */
    protected int duration;
    /** buff生效的角色条件 */
    protected BuffActiveCondition buffActiveCondition;
    /** buff 属性计算区 */
    protected AttributeActiveSection attributeActiveSection;

    public AbstractBuff(String name, String displayName, boolean enable, int priority, double chance, int duration, BuffActiveCondition bac, AttributeActiveSection aas) {
        this.name = name;
        this.displayName = displayName;
        this.enable = enable;
        this.priority = priority;
        this.chance = chance;
        this.duration = duration;
        this.buffActiveCondition = bac;
        this.attributeActiveSection = aas;
    }

    public AbstractBuff(String name, ConfigurationSection cfg, BuffActiveCondition bac, AttributeActiveSection aas) {
        this.name = name;
        this.displayName = cfg.getString(DISPLAY_NAME, name);
        this.enable = cfg.getBoolean(ENABLE, true);
        this.priority = cfg.getInt(PRIORITY, Integer.MAX_VALUE);
        this.chance = cfg.getDouble(CHANCE, 1D);
        this.duration = cfg.getInt(DURATION, 0);
        this.attributeActiveSection = aas;
        this.buffActiveCondition = bac;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public boolean enabled() {
        return this.enable;
    }

    @Override
    public double getChance() {
        return this.chance;
    }

    @Override
    public void setChance(double chance) {
        this.chance = chance;
    }

    @Override
    public int getDuration() {
        return this.duration;
    }

    @Override
    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public boolean isActiveUnder(BuffActiveCondition condition) {
        if (this.buffActiveCondition == BuffActiveCondition.ALL) return true;
        return condition.name().toLowerCase().startsWith(this.buffActiveCondition.name().toLowerCase());
    }

    @Override
    public AbstractBuff clone() {
        try {
            AbstractBuff clone = (AbstractBuff) super.clone();
            clone.name = this.name;
            clone.displayName = this.displayName;
            clone.enable = this.enable;
            clone.priority = this.priority;
            clone.chance = this.chance;
            clone.duration = this.duration;
            clone.attributeActiveSection = this.attributeActiveSection;
            clone.buffActiveCondition = this.buffActiveCondition;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
