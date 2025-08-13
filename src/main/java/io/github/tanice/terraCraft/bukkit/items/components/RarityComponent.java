package io.github.tanice.terraCraft.bukkit.items.components;

import de.tr7zw.nbtapi.NBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.vanilla.TerraRarityComponent;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;

import javax.annotation.Nullable;

import static io.github.tanice.terraCraft.core.utils.EnumUtil.safeValueOf;

public class RarityComponent implements TerraRarityComponent {
    @Nullable
    private final Rarity rarity;

    public RarityComponent(@Nullable String rarity) {
        if (rarity == null) this.rarity = null;
        else this.rarity = safeValueOf(Rarity.class, rarity, Rarity.COMMON);
    }

    @Override
    public void apply(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt ->{
                if (rarity != null) nbt.getOrCreateCompound(COMPONENT_KEY).setString(MINECRAFT_PREFIX + "rarity", rarity.name().toLowerCase());
            });
        } else TerraCraftLogger.warning("Rarity contents component is only supported in Minecraft 1.20.5 or newer versions");

    }
}
