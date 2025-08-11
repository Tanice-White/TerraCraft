package io.github.tanice.terraCraft.bukkit.items.components;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.TerraBreakSound;
import io.github.tanice.terraCraft.bukkit.utils.adapter.BukkitSound;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import io.github.tanice.terraCraft.core.utils.namespace.TerraNamespaceKey;

public class BreakSound extends BukkitSound implements TerraBreakSound {

    public BreakSound(float range, String namespace, String soundKey) {
        super(range, new TerraNamespaceKey(namespace, soundKey));
    }

    @Override
    public void apply(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                ReadWriteNBT component = nbt.getOrCreateCompound(COMPONENT_KEY).getOrCreateCompound(MINECRAFT_PREFIX + "break_sound");
                component.setFloat("range", range);
                component.setString("sound_id", SoundNamespaceKey.get());
            });
        } else TerraCraftLogger.error("break sound component is only supported in Minecraft 1.21.5 or newer versions");
    }
}
