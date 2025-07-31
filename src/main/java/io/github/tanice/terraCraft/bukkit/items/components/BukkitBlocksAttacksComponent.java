package io.github.tanice.terraCraft.bukkit.items.components;

import io.github.tanice.terraCraft.api.items.components.TerraBlocksAttacksComponent;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import io.papermc.paper.datacomponent.item.BlocksAttacks;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

/**
 * 1.21.6+
 */
public class BukkitBlocksAttacksComponent implements TerraBlocksAttacksComponent {



    @Override
    public void apply(ItemMeta meta) {
        Objects.requireNonNull(meta, "meta should not be null");
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_6)) {
            BlocksAttacks blocksAttacks = meta.getBlocksAttacks();

        } else TerraCraftLogger.error("BlocksAttacksComponent requires Minecraft version 1.21.6+");
    }
}
