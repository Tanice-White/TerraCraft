package io.github.tanice.terraCraft.bukkit.items.components;

import de.tr7zw.nbtapi.NBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.vanilla.TerraRepairComponent;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import io.github.tanice.terraCraft.core.utils.namespace.TerraNamespaceKey;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Repairable + RepairCost
 */
public class RepairComponent implements TerraRepairComponent {
    @Nullable
    private final Integer cost;
    private final List<TerraNamespaceKey> items;

    public RepairComponent(@Nullable Integer cost, List<TerraNamespaceKey> items) {
        this.cost = cost;
        this.items = items;
    }

    @Override
    public void apply(TerraBaseItem item) {
        if (cost != null) {
            if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
                NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                    nbt.getOrCreateCompound(COMPONENT_KEY).setInteger(MINECRAFT_PREFIX + "repair_cost", cost);
                });
            } else TerraCraftLogger.warning("Repair cost component is only supported in Minecraft 1.20.5 or newer versions");
        }

        if (item != null && !items.isEmpty()) {
            if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
                NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                    nbt.getOrCreateCompound(COMPONENT_KEY)
                            .getOrCreateCompound(MINECRAFT_PREFIX + "repairable")
                            .getStringList("items").addAll(items.stream().map(TerraNamespaceKey::get).toList());
                });
            } else TerraCraftLogger.warning("Repairable component is only supported in Minecraft 1.21.2 or newer versions");
        }
    }
}
