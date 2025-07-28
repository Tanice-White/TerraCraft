package io.github.tanice.terraCraft.core.attribute;

import io.github.tanice.terraCraft.api.attribute.AttributeActiveSection;
import io.github.tanice.terraCraft.api.attribute.AttributeType;
import io.github.tanice.terraCraft.api.attribute.DamageFromType;
import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Objects;

public class CalculableMeta implements TerraCalculableMeta, Cloneable {

    private static final int ATTRIBUTE_TYPE_COUNT = AttributeType.values().length;
    private static final int DAMAGE_TYPE_COUNT = DamageFromType.values().length;

    /** 索引对应AttributeType的ordinal() */
    private final double[] attributeModifiers;
    /** 索引对应DamageFromType的ordinal() */
    private final double[] damageTypeModifiers;
    /** 计算区枚举 */
    private AttributeActiveSection activeSection;

    public CalculableMeta() {
        this.attributeModifiers = new double[ATTRIBUTE_TYPE_COUNT];
        this.damageTypeModifiers = new double[DAMAGE_TYPE_COUNT];
        this.activeSection = AttributeActiveSection.INNER;
    }

    /**
     * 根据配置文件生成属性
     * @param cfg attr下的配置
     */
    public CalculableMeta(ConfigurationSection cfg, AttributeActiveSection activeSection) {
        this.attributeModifiers = new double[ATTRIBUTE_TYPE_COUNT];
        this.damageTypeModifiers = new double[DAMAGE_TYPE_COUNT];
        this.activeSection = activeSection;

        if (cfg == null) return;
        for (AttributeType type : AttributeType.values()) {
            int index = type.ordinal();
            this.attributeModifiers[index] = cfg.getDouble(type.name().toLowerCase(), 0D);
        }
        for (DamageFromType type : DamageFromType.values()) {
            int index = type.ordinal();
            this.damageTypeModifiers[index] = cfg.getDouble(type.name().toLowerCase(), 0D);
        }
    }

    @Override
    public void add(TerraCalculableMeta another, int k) {
        double[] otherAttrMods = another.getAttributeModifierArray();
        double[] otherDamageMods = another.getDamageTypeModifierArray();
        int index;
        for (AttributeType type : AttributeType.values()) {
            index = type.ordinal();
            this.attributeModifiers[index] += otherAttrMods[k] * k;
        }

        for (DamageFromType type : DamageFromType.values()) {
            index = type.ordinal();
            this.damageTypeModifiers[index] += otherDamageMods[k] * k;
        }
    }

    @Override
    public void multiply(TerraCalculableMeta another, int k) {
        double[] otherAttrMods = another.getAttributeModifierArray();
        double[] otherDamageMods = another.getDamageTypeModifierArray();
        int index;
        for (AttributeType type : AttributeType.values()) {
            index = type.ordinal();
            this.attributeModifiers[index] *= 1 + otherAttrMods[k] * k;
        }

        for (DamageFromType type : DamageFromType.values()) {
            index = type.ordinal();
            this.damageTypeModifiers[index] *= 1 + otherDamageMods[k] * k;
        }
    }

    @Override
    public double get(AttributeType type) {
        return this.attributeModifiers[type.ordinal()];
    }

    @Override
    public double get(DamageFromType type) {
        return this.damageTypeModifiers[type.ordinal()];
    }

    @Override
    public double[] getAttributeModifierArray() {
        return this.attributeModifiers;
    }

    @Override
    public double[] getDamageTypeModifierArray() {
        return this.damageTypeModifiers;
    }

    @Override
    public void setAttributeActiveSection(AttributeActiveSection section) {
        Objects.requireNonNull(section, "AttributeActiveSection should not be null when set");
        this.activeSection = section;
    }

    @Override
    public AttributeActiveSection getActiveSection() {
        return this.activeSection;
    }

    /**
     * 克隆方法优化：数组克隆比Map克隆更高效
     */
    @Override
    public CalculableMeta clone() {
        try {
            CalculableMeta clone = (CalculableMeta) super.clone();
            System.arraycopy(this.attributeModifiers, 0, clone.attributeModifiers, 0, ATTRIBUTE_TYPE_COUNT);
            System.arraycopy(this.damageTypeModifiers, 0, clone.damageTypeModifiers, 0, DAMAGE_TYPE_COUNT);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
