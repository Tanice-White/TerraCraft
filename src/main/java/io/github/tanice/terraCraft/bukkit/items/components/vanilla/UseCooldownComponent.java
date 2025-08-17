package io.github.tanice.terraCraft.bukkit.items.components.vanilla;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.vanilla.TerraUseCooldownComponent;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import io.github.tanice.terraCraft.core.utils.namespace.TerraNamespaceKey;
import org.bukkit.configuration.ConfigurationSection;

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
    public void apply(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                ReadWriteNBT compound = nbt.getOrCreateCompound(COMPONENT_KEY).getOrCreateCompound(MINECRAFT_PREFIX + "use_cooldown");
                compound.setFloat("seconds", seconds);
                if (group != null) compound.setString("cooldown_group", group.get());
            });
        } else TerraCraftLogger.warning("Use cooldown component is only supported in Minecraft 1.21.2 or newer versions");
    }

    public static void clear(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.getOrCreateCompound(COMPONENT_KEY).removeKey(MINECRAFT_PREFIX + "use_cooldown");
            });
        }
    }

    public static void remove(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.getOrCreateCompound(COMPONENT_KEY).removeKey(MINECRAFT_PREFIX + "use_cooldown");
                nbt.getOrCreateCompound(COMPONENT_KEY).getOrCreateCompound("!" + MINECRAFT_PREFIX + "use_cooldown");
            });
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, seconds);
    }
}
