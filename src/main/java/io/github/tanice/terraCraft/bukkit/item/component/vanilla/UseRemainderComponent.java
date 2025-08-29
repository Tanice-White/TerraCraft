package io.github.tanice.terraCraft.bukkit.item.component.vanilla;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.github.tanice.terraCraft.api.item.component.vanilla.TerraUseRemainderComponent;
import io.github.tanice.terraCraft.bukkit.util.version.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.util.version.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import io.github.tanice.terraCraft.core.util.namespace.TerraNamespaceKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Objects;

public class UseRemainderComponent implements TerraUseRemainderComponent {

    private final TerraNamespaceKey itemId;
    @Nullable
    private final ReadWriteNBT component;
    @Nullable
    private final Integer counts;

    public UseRemainderComponent(TerraNamespaceKey itemId, @Nullable String componentString, @Nullable Integer counts) {
        this.itemId = itemId;
        this.component = componentString == null ? null : NBT.parseNBT(componentString);
        this.counts = counts;
    }

    public UseRemainderComponent(ConfigurationSection cfg) {
        this(
                TerraNamespaceKey.from(cfg.getString("id")),
                cfg.isSet("component") ? cfg.getString("component") : null,
                cfg.isSet("count") ? cfg.getInt("count") : null
        );
    }

    @Override
    public void cover(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
            NBT.modifyComponents(item, nbt -> {
                ReadWriteNBT compound = nbt.getOrCreateCompound(MINECRAFT_PREFIX + "use_remainder");
                compound.setString("id", itemId.get());
                if (counts != null) compound.setInteger("count", counts);
                if (component != null) compound.getOrCreateCompound("components").mergeCompound(component);
            });
        } else TerraCraftLogger.warning("Use remainder component is only supported in Minecraft 1.21.2 or newer versions");
    }

    @Override
    public String getComponentName() {
        return "use_remainder";
    }

    public static void clear(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
            NBT.modifyComponents(item, nbt -> {
                nbt.removeKey(MINECRAFT_PREFIX + "use_remainder");
            });
        }
    }

    public static void remove(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
            NBT.modifyComponents(item, nbt -> {
                nbt.removeKey(MINECRAFT_PREFIX + "use_remainder");
                nbt.getOrCreateCompound("!" + MINECRAFT_PREFIX + "use_remainder");
            });
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, component, counts);
    }
}
