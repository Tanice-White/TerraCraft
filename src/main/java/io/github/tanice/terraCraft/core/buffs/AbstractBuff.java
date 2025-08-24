package io.github.tanice.terraCraft.core.buffs;

import io.github.tanice.terraCraft.api.buffs.BuffActiveCondition;
import io.github.tanice.terraCraft.api.buffs.TerraBaseBuff;
import io.github.tanice.terraCraft.api.attribute.AttributeActiveSection;
import io.github.tanice.terraCraft.bukkit.utils.StringUtil;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static io.github.tanice.terraCraft.api.commands.TerraCommand.*;

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
    /** buff 冲突和覆盖 */
    @Nullable
    protected Set<String> mutex;
    @Nullable
    protected Set<String> override;

    public AbstractBuff(String name, String displayName, boolean enable, int priority, double chance, int duration, @Nullable Set<String> mutex, Collection<String> override, BuffActiveCondition bac, AttributeActiveSection aas) {
        this.name = name;
        this.displayName = displayName;
        this.enable = enable;
        this.priority = priority;
        this.chance = chance;
        this.duration = duration;
        this.mutex = mutex;
        this.override = new HashSet<>(override);
        this.buffActiveCondition = bac;
        this.attributeActiveSection = aas;
    }

    public AbstractBuff(String name, ConfigurationSection cfg, @Nullable Set<String> mutex, BuffActiveCondition bac, AttributeActiveSection aas) {
        this.name = name;
        this.displayName = cfg.getString("display_name", name);
        this.enable = cfg.getBoolean("enable", true);
        this.priority = cfg.getInt("priority", Integer.MAX_VALUE);
        this.chance = cfg.getDouble("chance", 1D);
        this.duration = cfg.getInt("duration", 0);
        this.mutex = mutex;
        this.override = new HashSet<>(StringUtil.splitByComma(cfg.getString("override")));
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
    public boolean mutexWith(String buffName) {
        if (this.mutex == null || this.mutex.isEmpty()) return false;
        return this.mutex.contains(buffName);
    }

    @Override
    public boolean mutexWith(Set<String> buffNames) {
        if (mutex == null) return false;
        for (String s : buffNames) {
            if (this.mutex.contains(s)) return true;
        }
        return false;
    }

    @Override
    public boolean canOverride(String buffName) {
        if (this.override == null || this.override.isEmpty()) return false;
        return this.override.contains(buffName);
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(BOLD).append(YELLOW).append("Buff Details in ").append(name).append(":").append("\n");
        sb.append(AQUA).append("Name:").append(WHITE).append(name).append("\n");
        sb.append(AQUA).append("Display Name:").append(WHITE).append(displayName).append("\n");
        sb.append(AQUA).append("Enabled:").append(WHITE).append(enable).append("\n");
        sb.append(AQUA).append("Priority:").append(WHITE).append(priority).append("\n");
        sb.append(AQUA).append("Chance:").append(WHITE).append(chance).append("\n");
        sb.append(AQUA).append("Duration:").append(WHITE).append(duration).append("\n");
        sb.append(AQUA).append("Condition:").append(WHITE).append(buffActiveCondition).append("\n");
        sb.append(AQUA).append("Section:").append(WHITE).append(attributeActiveSection).append("\n");
        sb.append(AQUA).append("Mutex:").append(WHITE);
        if (mutex != null && !mutex.isEmpty()) sb.append(String.join(", ", mutex));
        else sb.append("None");
        sb.append("\n");
        sb.append(AQUA).append("Override Buffs:").append(WHITE);
        if (override != null && !override.isEmpty()) sb.append(String.join(", ", override));
        else sb.append("None");
        sb.append(RESET);
        return sb.toString();
    }
}
