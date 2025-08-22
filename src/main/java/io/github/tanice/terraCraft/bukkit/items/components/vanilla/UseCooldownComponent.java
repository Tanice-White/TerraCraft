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
    public void apply(ItemStack item) {
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

    public static void clear(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.removeKey(MINECRAFT_PREFIX + "use_cooldown");
            });
        }
    }

    public static void remove(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
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
