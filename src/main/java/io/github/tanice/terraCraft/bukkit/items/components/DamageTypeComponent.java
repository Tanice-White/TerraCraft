package io.github.tanice.terraCraft.bukkit.items.components;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import io.github.tanice.terraCraft.api.attribute.DamageFromType;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.ComponentState;
import io.github.tanice.terraCraft.api.items.components.TerraDamageTypeComponent;
import io.github.tanice.terraCraft.api.items.components.AbstractItemComponent;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

import java.util.Objects;

import static io.github.tanice.terraCraft.core.utils.EnumUtil.safeValueOf;

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
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            return NBT.getComponents(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".damage_from");
                if (data == null) return null;
                return new DamageTypeComponent(data.getString("type"), new ComponentState(data.getByte("state")));
            });
        } else {
            return NBT.get(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(TERRA_COMPONENT_KEY + ".damage_from");
                if (data == null) return null;
                return new DamageTypeComponent(data.getString("type"), new ComponentState(data.getByte("state")));
            });
        }
    }

    @Override
    public void doApply(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt -> {
                ReadWriteNBT component = nbt.resolveOrCreateCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".damage_from");
                component.setString("type", type.name().toLowerCase());
                component.setByte("state", state.toNbtByte());
            });
        } else {
            NBT.modify(item, nbt -> {
                ReadWriteNBT component = nbt.resolveOrCreateCompound(TERRA_COMPONENT_KEY + ".damage_from");
                component.setString("type", type.name().toLowerCase());
                component.setByte("state", state.toNbtByte());
            });
        }
    }

    @Override
    public void callEvent() {

    }

    @Override
    public void updateLore() {

    }

    public static void clear(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.resolveOrCreateCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY).removeKey("damage_from");
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt -> {
                nbt.resolveOrCreateCompound(TERRA_COMPONENT_KEY).removeKey("damage_from");
            });
        }
    }

    public static void remove(TerraBaseItem item) {
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
}
