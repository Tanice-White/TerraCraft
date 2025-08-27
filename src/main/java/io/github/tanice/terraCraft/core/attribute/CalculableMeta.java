package io.github.tanice.terraCraft.core.attribute;

import io.github.tanice.terraCraft.api.attribute.AttributeActiveSection;
import io.github.tanice.terraCraft.api.attribute.AttributeType;
import io.github.tanice.terraCraft.api.attribute.DamageFromType;
import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import io.github.tanice.terraCraft.core.util.TerraUtil;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;
import java.util.Objects;

import static io.github.tanice.terraCraft.api.command.TerraCommand.*;

public class CalculableMeta implements TerraCalculableMeta, Cloneable {

    private static final int ATTRIBUTE_TYPE_COUNT = AttributeType.values().length;
    private static final int DAMAGE_TYPE_COUNT = DamageFromType.values().length;

    /** 索引对应AttributeType的ordinal() */
    private double[] attributeModifiers;
    /** 索引对应DamageFromType的ordinal() */
    private double[] damageTypeModifiers;
    /** 计算区枚举 */
    private AttributeActiveSection activeSection;

    /**
     * from NBT
     */
    public CalculableMeta(double[] attributeModifiers, double[] damageTypeModifiers, AttributeActiveSection activeSection) {
        this.attributeModifiers = attributeModifiers;
        this.damageTypeModifiers = damageTypeModifiers;
        this.activeSection = activeSection;
    }

    public CalculableMeta(AttributeActiveSection aac) {
        this.attributeModifiers = new double[ATTRIBUTE_TYPE_COUNT];
        this.damageTypeModifiers = new double[DAMAGE_TYPE_COUNT];
        this.activeSection = aac;
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
        if (k == 0) return;
        double[] otherAttrMods = another.getAttributeModifierArray();
        double[] otherDamageMods = another.getDamageTypeModifierArray();

        int maxAttrLength = Math.max(this.attributeModifiers.length, (otherAttrMods != null ? otherAttrMods.length : 0));
        this.attributeModifiers = TerraUtil.extendArray(this.attributeModifiers, maxAttrLength);
        double[] extendedOtherAttr = TerraUtil.extendArray(otherAttrMods, maxAttrLength);

        int maxDamageLength = Math.max(this.damageTypeModifiers.length, (otherDamageMods != null ? otherDamageMods.length : 0));
        this.damageTypeModifiers = TerraUtil.extendArray(this.damageTypeModifiers, maxDamageLength);
        double[] extendedOtherDamage = TerraUtil.extendArray(otherDamageMods, maxDamageLength);

        int index;
        for (AttributeType type : AttributeType.values()) {
            index = type.ordinal();
            if (index < this.attributeModifiers.length && index < extendedOtherAttr.length) {
                this.attributeModifiers[index] += extendedOtherAttr[index] * k;
            }
        }

        for (DamageFromType type : DamageFromType.values()) {
            index = type.ordinal();
            if (index < this.damageTypeModifiers.length && index < extendedOtherDamage.length) {
                this.damageTypeModifiers[index] += extendedOtherDamage[index] * k;
            }
        }
    }

    @Override
    public void multiply(TerraCalculableMeta another, int k) {
        double[] otherAttrMods = another.getAttributeModifierArray();
        double[] otherDamageMods = another.getDamageTypeModifierArray();

        int maxAttrLength = Math.max(this.attributeModifiers.length, (otherAttrMods != null ? otherAttrMods.length : 0));
        this.attributeModifiers = TerraUtil.extendArray(this.attributeModifiers, maxAttrLength);
        double[] extendedOtherAttr = TerraUtil.extendArray(otherAttrMods, maxAttrLength);

        int maxDamageLength = Math.max(this.damageTypeModifiers.length, (otherDamageMods != null ? otherDamageMods.length : 0));
        this.damageTypeModifiers = TerraUtil.extendArray(this.damageTypeModifiers, maxDamageLength);
        double[] extendedOtherDamage = TerraUtil.extendArray(otherDamageMods, maxDamageLength);


        int index;
        for (AttributeType type : AttributeType.values()) {
            index = type.ordinal();
            if (index < this.attributeModifiers.length && index < extendedOtherAttr.length) {
                this.attributeModifiers[index] *= 1 + extendedOtherAttr[index] * k;
            }
        }

        for (DamageFromType type : DamageFromType.values()) {
            index = type.ordinal();
            if (index < this.damageTypeModifiers.length && index < extendedOtherDamage.length) {
                this.damageTypeModifiers[index] *= 1 + extendedOtherDamage[index] * k;
            }
        }
    }

    @Override
    public TerraCalculableMeta selfMultiply(int k) {
        for (AttributeType type : AttributeType.values()) {
            this.attributeModifiers[type.ordinal()] *= k;
        }
        for (DamageFromType type : DamageFromType.values()) {
            this.damageTypeModifiers[type.ordinal()] *= k;
        }
        return this;
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
            clone.attributeModifiers = Arrays.copyOf(this.attributeModifiers, this.attributeModifiers.length);
            clone.damageTypeModifiers = Arrays.copyOf(this.damageTypeModifiers, this.damageTypeModifiers.length);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(attributeModifiers), Arrays.hashCode(damageTypeModifiers), activeSection);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(BOLD).append(YELLOW).append("meta:").append("\n");
        sb.append(AQUA).append("    ").append("attributes:").append("\n");
        AttributeType[] attributeTypes = AttributeType.values();
        for (int i = 0; i < attributeTypes.length; i++) {
            if (i < attributeModifiers.length) {
                sb.append("        ").append(AQUA).append(attributeTypes[i].toString().toLowerCase()).append(":")
                        .append(WHITE).append(attributeModifiers[i]).append("\n");
            }
        }

        sb.append(AQUA).append("    ").append("damage types:").append(RESET).append("\n");
        DamageFromType[] damageTypes = DamageFromType.values();
        for (int i = 0; i < damageTypes.length; i++) {
            if (i < damageTypeModifiers.length) {
                sb.append("        ").append(AQUA).append(damageTypes[i].toString().toLowerCase()).append(":")
                        .append(WHITE).append(damageTypeModifiers[i]).append("\n");
            }
        }
        sb.append(AQUA).append("    ").append("active section:").append(WHITE).append(activeSection.toString().toLowerCase()).append(RESET);
        return sb.toString();
    }
}
