package io.github.tanice.terraCraft.bukkit.items.components.vanilla;

import de.tr7zw.nbtapi.NBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.vanilla.TerraJukeboxPlayable;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import io.github.tanice.terraCraft.core.utils.namespace.TerraNamespaceKey;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class JukeboxPlayable implements TerraJukeboxPlayable {
    private final TerraNamespaceKey musicId;

    public JukeboxPlayable(String musicId) {
        this.musicId = TerraNamespaceKey.from(musicId);
    }

    @Override
    public void apply(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21)) {
            NBT.modifyComponents(item, nbt -> {
                if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_5)) {
                    nbt.setString(MINECRAFT_PREFIX + "jukebox_playable", musicId.get());
                } else {
                    nbt.getOrCreateCompound(MINECRAFT_PREFIX + "jukebox_playable").setString("song", musicId.get());
                }
            });
        } else TerraCraftLogger.warning("Jukebox playable component is only supported in Minecraft 1.21 or newer versions");
    }

    public static void clear(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.removeKey(MINECRAFT_PREFIX + "jukebox_playable");
            });
        }
    }

    public static void remove(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.removeKey(MINECRAFT_PREFIX + "jukebox_playable");
                nbt.getOrCreateCompound("!" + MINECRAFT_PREFIX + "jukebox_playable");
            });
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(musicId);
    }
}
