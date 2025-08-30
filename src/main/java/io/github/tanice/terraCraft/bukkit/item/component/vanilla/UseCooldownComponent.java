package io.github.tanice.terraCraft.bukkit.item.component.vanilla;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.github.tanice.terraCraft.api.item.component.vanilla.TerraUseCooldownComponent;
import io.github.tanice.terraCraft.bukkit.util.version.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.util.version.ServerVersion;
import io.github.tanice.terraCraft.core.util.logger.TerraCraftLogger;
import io.github.tanice.terraCraft.core.util.namespace.TerraNamespaceKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Objects;

public class UseCooldownComponent implements TerraUseCooldownComponent {
    @Nullable
    private final TerraNamespaceKey group;
    private final float seconds;

    public UseCooldownComponent(@Nullable TerraNamespaceKey group, float seconds) {
        this.group = group;
        this.seconds = seconds;
    }

    public UseCooldownComponent(ConfigurationSection cfg) {
        this(
                TerraNamespaceKey.from(cfg.getString("group")),
                (float) cfg.getDouble("seconds")
        );
    }

    @Override
    public void cover(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
            NBT.modifyComponents(item, nbt -> {
                ReadWriteNBT compound = nbt.getOrCreateCompound(MINECRAFT_PREFIX + "use_cooldown");
                compound.setFloat("seconds", seconds);
                if (group != null) compound.setString("cooldown_group", group.get());
            });
        } else TerraCraftLogger.warning("Use cooldown component is only supported in Minecraft 1.21.2 or newer versions");
    }

    @Override
    public String getComponentName() {
        return "use_cooldown";
    }

    public static void clear(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
            NBT.modifyComponents(item, nbt -> {
                nbt.removeKey(MINECRAFT_PREFIX + "use_cooldown");
            });
        }
    }

    public static void remove(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
            NBT.modifyComponents(item, nbt -> {
                nbt.removeKey(MINECRAFT_PREFIX + "use_cooldown");
                nbt.getOrCreateCompound("!" + MINECRAFT_PREFIX + "use_cooldown");
            });
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, seconds);
    }
}
