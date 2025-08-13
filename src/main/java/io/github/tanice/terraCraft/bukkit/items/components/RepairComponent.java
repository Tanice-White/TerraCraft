package io.github.tanice.terraCraft.bukkit.items.components;

import de.tr7zw.nbtapi.NBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.TerraRepairComponent;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import io.github.tanice.terraCraft.core.utils.namespace.TerraNamespaceKey;

import java.util.Arrays;

/**
 * Repairable + RepairCost
 */
public class RepairComponent implements TerraRepairComponent {

    private final Integer cost;
    private final TerraNamespaceKey[] items;

    public RepairComponent(Integer cost, TerraNamespaceKey[] items) {
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

        if (item != null && items.length > 0) {
            if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
                NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                    nbt.getOrCreateCompound(COMPONENT_KEY)
                            .getOrCreateCompound(MINECRAFT_PREFIX + "repairable")
                            .getStringList("items").addAll(Arrays.stream(items).map(TerraNamespaceKey::get).toList());
                });
            } else TerraCraftLogger.warning("Repairable component is only supported in Minecraft 1.21.2 or newer versions");
        }
    }
}
