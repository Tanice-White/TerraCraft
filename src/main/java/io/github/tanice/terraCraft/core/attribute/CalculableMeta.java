package io.github.tanice.terraCraft.core.attribute;

import io.github.tanice.terraCraft.api.attribute.AttributeActiveSection;
import io.github.tanice.terraCraft.api.attribute.AttributeType;
import io.github.tanice.terraCraft.api.attribute.DamageFromType;
import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import org.bukkit.configuration.ConfigurationSection;

public class CalculableMeta implements TerraCalculableMeta, Cloneable {

    private static final int ATTRIBUTE_TYPE_COUNT = AttributeType.values().length;
    private static final int DAMAGE_TYPE_COUNT = DamageFromType.values().length;

    /** 索引对应AttributeType的ordinal() */
    private final double[] attributeModifiers;
    /** 索引对应DamageFromType的ordinal() */
    private final double[] damageTypeModifiers;
    /** 计算区枚举 */
    private final AttributeActiveSection activeSection;

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
            attributeModifiers[index] = cfg.getDouble(type.name().toLowerCase(), 0D);
        }
        for (DamageFromType type : DamageFromType.values()) {
            int index = type.ordinal();
            damageTypeModifiers[index] = cfg.getDouble(type.name().toLowerCase(), 0D);
        }
    }

    /**
     * 合并属性值
     */
    @Override
    public void merge(TerraCalculableMeta meta, int k) {
        CalculableMeta other = (CalculableMeta) meta;
        for (int i = 0; i < ATTRIBUTE_TYPE_COUNT; i++) {
            attributeModifiers[i] += other.attributeModifiers[i] * k;
        }
        for (int i = 0; i < DAMAGE_TYPE_COUNT; i++) {
            damageTypeModifiers[i] += other.damageTypeModifiers[i] * k;
        }
    }

    @Override
    public double get(AttributeType type) {
        return attributeModifiers[type.ordinal()];
    }

    @Override
    public double get(DamageFromType type) {
        return damageTypeModifiers[type.ordinal()];
    }

    @Override
    public double[] getAttributeModifierArray() {
        return attributeModifiers;
    }

    @Override
    public double[] getDamageTypeModifierArray() {
        return damageTypeModifiers;
    }

    @Override
    public AttributeActiveSection getActiveSection() {
        return activeSection;
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
