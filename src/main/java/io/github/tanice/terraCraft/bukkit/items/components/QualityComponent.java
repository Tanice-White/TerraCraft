package io.github.tanice.terraCraft.bukkit.items.components;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
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

    public QualityComponent(@Nullable String quality, @Nullable String qualityGroup) {
        this.quality = quality;
        this.qualityGroup = qualityGroup;
    }

    public static QualityComponent from(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            return NBT.getComponents(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(COMPONENT_KEY + "." + MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".quality");
                if (data == null) return null;
                return new QualityComponent(data.getString("quality"), data.getString("group"));
            });
        } else {
            return NBT.get(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(TAG_KEY + "." + TERRA_COMPONENT_KEY + ".quality");
                if (data == null) return null;
                return new QualityComponent(data.getString("quality"), data.getString("group"));
            });
        }
    }

    @Override
    public void apply(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                ReadWriteNBT data = nbt.resolveOrCreateCompound(COMPONENT_KEY + "." + MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".quality");
                if (quality != null) data.setString("quality", quality);
                if (qualityGroup != null) data.setString("group", qualityGroup);
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt -> {
                ReadWriteNBT data = nbt.resolveOrCreateCompound(TAG_KEY + "." + TERRA_COMPONENT_KEY + ".quality");
                if (quality != null) data.setString("quality", quality);
                if (qualityGroup != null) data.setString("group", qualityGroup);
            });
        }
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
}
