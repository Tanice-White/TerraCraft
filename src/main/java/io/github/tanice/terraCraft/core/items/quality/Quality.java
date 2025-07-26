package io.github.tanice.terraCraft.core.items.quality;

import io.github.tanice.terraCraft.api.attribute.AttributeActiveSection;
import io.github.tanice.terraCraft.api.attribute.AttributeType;
import io.github.tanice.terraCraft.api.attribute.DamageFromType;
import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import io.github.tanice.terraCraft.api.items.quality.TerraQuality;
import io.github.tanice.terraCraft.core.attribute.CalculableMeta;
import org.bukkit.configuration.ConfigurationSection;

import static io.github.tanice.terraCraft.core.constants.ConfigKeys.*;

public class Quality implements TerraQuality {

    private final String name;
    private final int weight;
    private final String displayName;
    private final Operation op;
    private final TerraCalculableMeta meta;

    public Quality(String name, int weight, String displayName, Operation op, ConfigurationSection cfg) {
        this.name = name;
        this.weight = weight;
        this.displayName = displayName;
        this.op = op;
        this.meta = new CalculableMeta(cfg.getConfigurationSection(ATTRIBUTE_SECTION), AttributeActiveSection.BASE);
    }

    @Override
    public void apply(TerraCalculableMeta other) {
        switch (op) {
            case ADD:
                this.addActive(other);
                return;
            case MULTIPLY:
                this.multiplyActive(other);
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getOriWeight() {
        return this.weight;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public Operation getOp() {
        return this.op;
    }

    @Override
    public TerraCalculableMeta getMeta() {
        return this.meta.clone();
    }

    private void addActive(TerraCalculableMeta other) {
        double[] otherAttrMods = other.getAttributeModifierArray();
        double[] thisAttrMods = this.meta.getAttributeModifierArray();

        for (AttributeType type : AttributeType.values()) {
            int index = type.ordinal();
            otherAttrMods[index] += thisAttrMods[index];
        }

        double[] otherDmgMods = other.getDamageTypeModifierArray();
        double[] thisDmgMods = this.meta.getDamageTypeModifierArray();

        for (DamageFromType type : DamageFromType.values()) {
            int index = type.ordinal();
            otherDmgMods[index] += thisDmgMods[index];
        }
    }

    private void multiplyActive(TerraCalculableMeta other) {
        double[] otherAttrMods = other.getAttributeModifierArray();
        double[] thisAttrMods = this.meta.getAttributeModifierArray();
        for (AttributeType type : AttributeType.values()) {
            int index = type.ordinal();
            otherAttrMods[index] *= (1 + thisAttrMods[index]);
        }

        double[] otherDmgMods = other.getDamageTypeModifierArray();
        double[] thisDmgMods = this.meta.getDamageTypeModifierArray();
        for (DamageFromType type : DamageFromType.values()) {
            int index = type.ordinal();
            otherDmgMods[index] *= (1 + thisDmgMods[index]);
        }
    }
}
