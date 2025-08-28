package io.github.tanice.terraCraft.bukkit.item.component;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBTList;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import io.github.tanice.terraCraft.api.attribute.AttributeActiveSection;
import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import io.github.tanice.terraCraft.api.item.TerraBaseItem;
import io.github.tanice.terraCraft.api.item.component.ComponentState;
import io.github.tanice.terraCraft.api.item.component.TerraMetaComponent;
import io.github.tanice.terraCraft.api.item.component.AbstractItemComponent;
import io.github.tanice.terraCraft.bukkit.util.version.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.util.version.ServerVersion;
import io.github.tanice.terraCraft.core.attribute.CalculableMeta;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static io.github.tanice.terraCraft.api.command.TerraCommand.*;
import static io.github.tanice.terraCraft.core.util.EnumUtil.safeValueOf;

/**
 * 携带 meta 属性
 * 如果有 GemComponent 则 meta 视为 gem 的 meta
 */
public class MetaComponent extends AbstractItemComponent implements TerraMetaComponent {

    private TerraCalculableMeta meta;

    public MetaComponent(TerraCalculableMeta meta, boolean updatable) {
        super(updatable);
        this.meta = meta;
    }

    public MetaComponent(TerraCalculableMeta meta, ComponentState state) {
        super(state);
        this.meta = meta;
    }

    public MetaComponent(ConfigurationSection cfg) {
        super(cfg.getBoolean("updatable", true));
        this.meta = new CalculableMeta(
                cfg.getConfigurationSection("attribute"),
                safeValueOf(AttributeActiveSection.class, cfg.getString("section"), AttributeActiveSection.ERROR)
        );
    }

    @Nullable
    public static MetaComponent from(ItemStack item) {
        if (item == null || item.isEmpty()) return null;
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            return NBT.getComponents(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".meta");
                if (data == null) return null;
                return fromNBT(data);
            });
        } else {
            return NBT.get(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(TERRA_COMPONENT_KEY + ".meta");
                if (data == null) return null;
                return fromNBT(data);
            });
        }
    }

    @Override
    public void doCover(ItemStack item) {
        clear(item);
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt -> {
                ReadWriteNBT data = nbt.resolveOrCreateCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".meta");
                addToCompound(data);
            });
        } else {
            NBT.modify(item, nbt -> {
                ReadWriteNBT data = nbt.resolveOrCreateCompound(TERRA_COMPONENT_KEY + ".meta");
                addToCompound(data);
            });
        }
    }

    @Override
    public void updateLore() {

    }

    public static void clear(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt -> {
                nbt.resolveOrCreateCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY).removeKey("meta");
            });
        } else {
            NBT.modify(item, nbt -> {
                nbt.resolveOrCreateCompound(TERRA_COMPONENT_KEY).removeKey("meta");
            });
        }
    }

    public static void remove(ItemStack item) {
        clear(item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(meta);
    }

    @Override
    public TerraCalculableMeta getMeta() {
        return this.meta;
    }

    @Override
    public void setMeta(TerraCalculableMeta meta) {
        this.meta = meta;
    }

    @Override
    public String toString() {
        return meta.toString() + "\n" + "    " + AQUA + "state:" + WHITE + state + RESET;
    }

    private void addToCompound(ReadWriteNBT compound) {
        compound.setString("active_section", meta.getActiveSection().name().toLowerCase());
        compound.getDoubleList("attribute").addAll(Arrays.stream(meta.getAttributeModifierArray()).boxed().toList());
        compound.getDoubleList("damage_type").addAll(Arrays.stream(meta.getDamageTypeModifierArray()).boxed().toList());
        compound.setByte("state", state.toNbtByte());
    }

    private static MetaComponent fromNBT(ReadableNBT nbt) {
        AttributeActiveSection as = safeValueOf(AttributeActiveSection.class, nbt.getString("active_section"), AttributeActiveSection.ERROR);
        List<Double> attributeList = nbt.getDoubleList("attribute").toListCopy();
        double[] attributes = attributeList.stream().mapToDouble(Double::doubleValue).toArray();
        List<Double> damageTypeList = nbt.getDoubleList("damage_type").toListCopy();
        double[] damageTypes = damageTypeList.stream().mapToDouble(Double::doubleValue).toArray();
        return new MetaComponent(new CalculableMeta(attributes, damageTypes, as), new ComponentState(nbt.getByte("state")));
    }

    @Override
    public String getComponentName() {
        return "terra_meta";
    }
}
