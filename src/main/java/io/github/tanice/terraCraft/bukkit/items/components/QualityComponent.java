package io.github.tanice.terraCraft.bukkit.items.components;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.ComponentState;
import io.github.tanice.terraCraft.api.items.components.TerraBaseComponent;
import io.github.tanice.terraCraft.api.items.components.TerraQualityComponent;
import io.github.tanice.terraCraft.bukkit.items.AbstractItemComponent;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public class QualityComponent extends AbstractItemComponent implements TerraQualityComponent {
    @Nullable
    private String qualityGroup;
    @Nullable
    private String quality;

    public QualityComponent(@Nullable String quality, @Nullable String qualityGroup, boolean updatable) {
        super(updatable);
        this.quality = quality;
        this.qualityGroup = qualityGroup;
    }

    public QualityComponent(@Nullable String quality, @Nullable String qualityGroup, ComponentState state) {
        super(state);
        this.quality = quality;
        this.qualityGroup = qualityGroup;
    }

    @Nullable
    public static QualityComponent from(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            return NBT.getComponents(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(COMPONENT_KEY + "." + MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".quality");
                if (data == null) return null;
                return fromNBT(data);
            });
        } else {
            return NBT.get(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(TAG_KEY + "." + TERRA_COMPONENT_KEY + ".quality");
                if (data == null) return null;
                return fromNBT(data);
            });
        }
    }

    @Override
    public void apply(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                ReadWriteNBT data = nbt.resolveOrCreateCompound(COMPONENT_KEY + "." + MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".quality");
                addToCompound(data);
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt -> {
                ReadWriteNBT data = nbt.resolveOrCreateCompound(TAG_KEY + "." + TERRA_COMPONENT_KEY + ".quality");
                addToCompound(data);
            });
        }
    }

    public static void clear(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.resolveOrCreateCompound(COMPONENT_KEY + "." + MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY).removeKey("quality");
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt -> {
                nbt.resolveOrCreateCompound(TAG_KEY + "." + TERRA_COMPONENT_KEY).removeKey("quality");
            });
        }
    }

    public static void remove(TerraBaseItem item) {
        clear(item);
    }

    @Override
    public void updatePartialFrom(TerraBaseComponent old) {
        QualityComponent oldComponent = (QualityComponent) old;
        if (oldComponent.state.isModified()) this.quality = oldComponent.quality;
    }

    @Override
    public @Nullable String getQuality() {
        return this.quality;
    }

    @Override
    public void setQuality(@Nullable String quality) {
        this.quality = quality;
    }

    @Override
    public @Nullable String getQualityGroup() {
        return this.qualityGroup;
    }

    @Override
    public void setQualityGroup(@Nullable String group) {
        this.qualityGroup = group;
    }

    private void addToCompound(ReadWriteNBT compound) {
        if (quality != null) compound.setString("value", quality);
        if (qualityGroup != null) compound.setString("group", qualityGroup);
        compound.setByte("state", state.toNbtByte());
    }

    private static QualityComponent fromNBT(ReadableNBT nbt) {
        return new QualityComponent(
                nbt.getString("value"),
                nbt.getString("group"),
                new ComponentState(nbt.getByte("state"))
        );
    }
}
