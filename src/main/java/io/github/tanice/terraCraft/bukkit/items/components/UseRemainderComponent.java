package io.github.tanice.terraCraft.bukkit.items.components;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.TerraUseRemainderComponent;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import io.github.tanice.terraCraft.core.utils.namespace.TerraNamespaceKey;

import javax.annotation.Nonnull;

public class UseRemainderComponent implements TerraUseRemainderComponent {

    @Nonnull
    private final TerraNamespaceKey itemId;
    private final ReadWriteNBT component;
    private final Integer counts;

    public UseRemainderComponent(@Nonnull TerraNamespaceKey itemId, ReadWriteNBT component, Integer counts) {
        this.itemId = itemId;
        this.component = component;
        this.counts = counts;
    }

    @Override
    public void apply(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                ReadWriteNBT compound = nbt.getOrCreateCompound(COMPONENT_KEY).getOrCreateCompound(MINECRAFT_PREFIX + "use_remainder");
                compound.setString("id", itemId.get());
                if (counts != null) compound.setInteger("count", counts);
                if (component != null) compound.getOrCreateCompound("components").mergeCompound(component);
            });
        } else TerraCraftLogger.warning("Use remainder component is only supported in Minecraft 1.21.2 or newer versions");

    }
}
