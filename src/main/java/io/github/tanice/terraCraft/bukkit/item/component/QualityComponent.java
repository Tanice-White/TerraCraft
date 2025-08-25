package io.github.tanice.terraCraft.bukkit.item.component;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import io.github.tanice.terraCraft.api.item.TerraBaseItem;
import io.github.tanice.terraCraft.api.item.component.ComponentState;
import io.github.tanice.terraCraft.api.item.component.TerraBaseComponent;
import io.github.tanice.terraCraft.api.item.component.TerraQualityComponent;
import io.github.tanice.terraCraft.api.item.component.AbstractItemComponent;
import io.github.tanice.terraCraft.bukkit.util.version.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.util.version.ServerVersion;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Objects;

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

    public QualityComponent(ConfigurationSection cfg) {
        super(cfg.getBoolean("updatable", true));
        this.qualityGroup = cfg.getString("group");
        this.quality = cfg.getString("quality");
    }

    @Nullable
    public static QualityComponent from(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            return NBT.getComponents(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".quality");
                if (data == null) return null;
                return fromNBT(data);
            });
        } else {
            return NBT.get(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(TERRA_COMPONENT_KEY + ".quality");
                if (data == null) return null;
                return fromNBT(data);
            });
        }
    }

    @Override
    public void doApply(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt -> {
                ReadWriteNBT data = nbt.resolveOrCreateCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".quality");
                addToCompound(data);
            });
        } else {
            NBT.modify(item, nbt -> {
                ReadWriteNBT data = nbt.resolveOrCreateCompound(TERRA_COMPONENT_KEY + ".quality");
                addToCompound(data);
            });
        }
    }

    @Override
    public void updateLore() {

    }

    public static void clear(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.resolveOrCreateCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY).removeKey("quality");
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt -> {
                nbt.resolveOrCreateCompound(TERRA_COMPONENT_KEY).removeKey("quality");
            });
        }
    }

    public static void remove(TerraBaseItem item) {
        clear(item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quality, qualityGroup);
    }

    @Override
    public String getComponentName() {
        return "quality";
    }

    @Override
    public TerraBaseComponent updatePartial() {
        return new QualityComponent(null, this.qualityGroup, this.state);
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
