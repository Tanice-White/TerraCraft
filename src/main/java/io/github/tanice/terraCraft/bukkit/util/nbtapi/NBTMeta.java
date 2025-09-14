package io.github.tanice.terraCraft.bukkit.util.nbtapi;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import io.github.tanice.terraCraft.api.attribute.AttributeActiveSection;
import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import io.github.tanice.terraCraft.core.attribute.CalculableMeta;

import java.util.Arrays;
import java.util.List;

import static io.github.tanice.terraCraft.core.util.EnumUtil.safeValueOf;

public class NBTMeta implements Cloneable {

    private TerraCalculableMeta meta;

    public NBTMeta(TerraCalculableMeta meta) {
        this.meta = meta;
    }

    public TerraCalculableMeta getMeta() {
        return meta;
    }

    public void addToCompound(ReadWriteNBT compound) {
        compound.setString("active_section", meta.getActiveSection().name().toLowerCase());
        compound.getDoubleList("attribute").clear();
        compound.getDoubleList("attribute").addAll(Arrays.stream(meta.getAttributeModifierArray()).boxed().toList());
        compound.getDoubleList("damage_type").clear();
        compound.getDoubleList("damage_type").addAll(Arrays.stream(meta.getDamageTypeModifierArray()).boxed().toList());
    }

    public static NBTMeta fromNBT(ReadableNBT nbt) {
        AttributeActiveSection as = safeValueOf(AttributeActiveSection.class, nbt.getString("active_section"), AttributeActiveSection.ERROR);
        List<Double> attributeList = nbt.getDoubleList("attribute").toListCopy();
        double[] attributes = attributeList.stream().mapToDouble(Double::doubleValue).toArray();
        List<Double> damageTypeList = nbt.getDoubleList("damage_type").toListCopy();
        double[] damageTypes = damageTypeList.stream().mapToDouble(Double::doubleValue).toArray();
        return new NBTMeta(new CalculableMeta(attributes, damageTypes, as));
    }

    @Override
    public NBTMeta clone() {
        try {
            NBTMeta clone = (NBTMeta) super.clone();
            clone.meta = this.meta.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
