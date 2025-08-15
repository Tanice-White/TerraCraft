package io.github.tanice.terraCraft.bukkit.items.components;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import io.github.tanice.terraCraft.api.attribute.DamageFromType;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.ComponentState;
import io.github.tanice.terraCraft.api.items.components.TerraDamageTypeComponent;
import io.github.tanice.terraCraft.bukkit.items.AbstractItemComponent;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

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

    @Nullable
    public static DamageTypeComponent from(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            return NBT.getComponents(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(COMPONENT_KEY + "." + MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".damage_from");
                if (data == null) return null;
                return new DamageTypeComponent(data.getString("type"), new ComponentState(data.getByte("state")));
            });
        } else {
            return NBT.get(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(TAG_KEY + "." + TERRA_COMPONENT_KEY + ".damage_from");
                if (data == null) return null;
                return new DamageTypeComponent(data.getString("type"), new ComponentState(data.getByte("state")));
            });
        }
    }

    @Override
    public void apply(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                ReadWriteNBT component = nbt.resolveOrCreateCompound(COMPONENT_KEY + "." + MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".damage_from");
                component.setString("type", type.name().toLowerCase());
                component.setByte("state", state.toNbtByte());
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt -> {
                ReadWriteNBT component = nbt.resolveOrCreateCompound(TAG_KEY + "." + TERRA_COMPONENT_KEY + ".damage_from");
                component.setString("type", type.name().toLowerCase());
                component.setByte("state", state.toNbtByte());
            });
        }
    }

    @Override
    public void clear(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.resolveOrCreateCompound(COMPONENT_KEY + "." + MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY).removeKey("damage_from");
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt -> {
                nbt.resolveOrCreateCompound(TAG_KEY + "." + TERRA_COMPONENT_KEY).removeKey("damage_from");
            });
        }
    }

    @Override
    public void remove(TerraBaseItem item) {
        this.clear(item);
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
