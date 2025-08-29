package io.github.tanice.terraCraft.bukkit.item.component;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import io.github.tanice.terraCraft.api.item.component.ComponentState;
import io.github.tanice.terraCraft.api.item.component.TerraBaseComponent;
import io.github.tanice.terraCraft.api.item.component.TerraQualityComponent;
import io.github.tanice.terraCraft.api.item.component.AbstractItemComponent;
import io.github.tanice.terraCraft.bukkit.util.version.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.util.version.ServerVersion;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.github.tanice.terraCraft.api.command.TerraCommand.*;

public class QualityComponent extends AbstractItemComponent implements TerraQualityComponent {

    private List<String> groups;
    @Nullable
    private String quality;

    public QualityComponent(@Nullable String quality, @Nullable List<String> groups, boolean updatable) {
        super(updatable);
        this.quality = quality;
        this.groups = groups;
    }

    public QualityComponent(@Nullable String quality, @Nullable List<String> groups, ComponentState state) {
        super(state);
        this.quality = quality;
        this.groups = groups;
    }

    public QualityComponent(ConfigurationSection cfg) {
        super(cfg.getBoolean("updatable", true));
        this.groups = cfg.getStringList("groups");
        this.quality = cfg.getString("default");
    }

    @Nullable
    public static QualityComponent from(ItemStack item) {
        if (item == null || item.isEmpty()) return null;
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
    public void doCover(ItemStack item) {
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

    public static void clear(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt -> {
                nbt.resolveOrCreateCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY).removeKey("quality");
            });
        } else {
            NBT.modify(item, nbt -> {
                nbt.resolveOrCreateCompound(TERRA_COMPONENT_KEY).removeKey("quality");
            });
        }
    }

    public static void remove(ItemStack item) {
        clear(item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quality, groups);
    }

    @Override
    public String getComponentName() {
        return "quality";
    }

    @Override
    public TerraBaseComponent updatePartial() {
        /* null 占位继承 */
        return new QualityComponent(null, this.groups, this.state);
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
    public List<String> getGroups() {
        return this.groups;
    }

    @Override
    public void setGroups(@Nullable List<String> group) {
        this.groups = group == null ? new ArrayList<>() : group;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(BOLD).append(YELLOW).append("quality:").append("\n");
        sb.append("    ").append(AQUA).append("groups:");
        if (groups != null && !groups.isEmpty()) {
            sb.append(WHITE).append(String.join(", ", groups));
        } else sb.append(GRAY).append("null");
        sb.append("\n");
        sb.append("    ").append(AQUA).append("value:");
        if (quality != null && !quality.isEmpty()) sb.append(WHITE).append(quality);
        else sb.append(GRAY).append("null");
        sb.append("\n");
        sb.append("    ").append(AQUA).append("state:").append(WHITE).append(state).append(RESET);
        return sb.toString();
    }

    private void addToCompound(ReadWriteNBT compound) {
        if (quality != null) compound.setString("value", quality);
        if (groups != null) {
            compound.getStringList("groups").clear();
            compound.getStringList("groups").addAll(groups);
        }
        compound.setByte("state", state.toNbtByte());
    }

    private static QualityComponent fromNBT(ReadableNBT nbt) {
        return new QualityComponent(
                nbt.getString("value"),
                nbt.getStringList("groups").toListCopy(),
                new ComponentState(nbt.getByte("state"))
        );
    }
}
