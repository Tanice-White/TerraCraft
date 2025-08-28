package io.github.tanice.terraCraft.bukkit.item.component;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import io.github.tanice.terraCraft.api.attribute.DamageFromType;
import io.github.tanice.terraCraft.api.item.component.ComponentState;
import io.github.tanice.terraCraft.api.item.component.TerraDamageTypeComponent;
import io.github.tanice.terraCraft.api.item.component.AbstractItemComponent;
import io.github.tanice.terraCraft.bukkit.util.version.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.util.version.ServerVersion;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

import java.util.Objects;

import static io.github.tanice.terraCraft.api.command.TerraCommand.*;
import static io.github.tanice.terraCraft.core.util.EnumUtil.safeValueOf;

public class DamageTypeComponent extends AbstractItemComponent implements TerraDamageTypeComponent {

    private DamageFromType type;

    public DamageTypeComponent(String type, boolean updatable) {
        super(updatable);
        this.type = safeValueOf(DamageFromType.class, type, DamageFromType.OTHER);
    }

    public DamageTypeComponent(String type, ComponentState state) {
        super(state);
        this.type = safeValueOf(DamageFromType.class, type, DamageFromType.OTHER);
    }

    public DamageTypeComponent(ConfigurationSection cfg) {
        super(cfg.getBoolean("updatable", true));
        this.type = safeValueOf(DamageFromType.class, cfg.getString("type"), DamageFromType.OTHER);
    }

    @Nullable
    public static DamageTypeComponent from(ItemStack item) {
        if (item == null || item.isEmpty()) return null;
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            return NBT.getComponents(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".damage_type");
                if (data == null) return null;
                return new DamageTypeComponent(data.getString("type"), new ComponentState(data.getByte("state")));
            });
        } else {
            return NBT.get(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(TERRA_COMPONENT_KEY + ".damage_type");
                if (data == null) return null;
                return new DamageTypeComponent(data.getString("type"), new ComponentState(data.getByte("state")));
            });
        }
    }

    @Override
    public void doCover(ItemStack item) {
        clear(item);
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt -> {
                ReadWriteNBT component = nbt.resolveOrCreateCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".damage_type");
                component.setString("type", type.name().toLowerCase());
                component.setByte("state", state.toNbtByte());
            });
        } else {
            NBT.modify(item, nbt -> {
                ReadWriteNBT component = nbt.resolveOrCreateCompound(TERRA_COMPONENT_KEY + ".damage_type");
                component.setString("type", type.name().toLowerCase());
                component.setByte("state", state.toNbtByte());
            });
        }
    }

    @Override
    public void updateLore() {

    }

    public static void clear(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt -> {
                nbt.resolveOrCreateCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY).removeKey("damage_type");
            });
        } else {
            NBT.modify(item, nbt -> {
                nbt.resolveOrCreateCompound(TERRA_COMPONENT_KEY).removeKey("damage_type");
            });
        }
    }

    public static void remove(ItemStack item) {
        clear(item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @Override
    public DamageFromType getType() {
        return this.type;
    }

    @Override
    public void setType(DamageFromType type) {
        this.type = type;
    }

    @Override
    public String getComponentName() {
        return "damage_type";
    }

    @Override
    public String toString() {
        return BOLD + YELLOW + "damage_type:" + RESET + "\n" +
                "    " + AQUA + "type:" + WHITE + type.name().toLowerCase() + "\n" +
                "    " + AQUA + "state:" + WHITE + state + RESET;
    }
}
