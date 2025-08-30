package io.github.tanice.terraCraft.bukkit.item.component.vanilla;

import de.tr7zw.nbtapi.NBT;
import io.github.tanice.terraCraft.api.item.component.vanilla.TerraRarityComponent;
import io.github.tanice.terraCraft.bukkit.util.version.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.util.version.ServerVersion;
import io.github.tanice.terraCraft.core.util.logger.TerraCraftLogger;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

import java.util.Objects;

import static io.github.tanice.terraCraft.core.util.EnumUtil.safeValueOf;

public class RarityComponent implements TerraRarityComponent {
    @Nullable
    private final Rarity rarity;

    public RarityComponent(@Nullable String rarity) {
        if (rarity == null) this.rarity = null;
        else this.rarity = safeValueOf(Rarity.class, rarity, Rarity.COMMON);
    }

    @Override
    public void cover(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt ->{
                if (rarity != null) nbt.setString(MINECRAFT_PREFIX + "rarity", rarity.name().toLowerCase());
            });
        } else TerraCraftLogger.warning("Rarity contents component is only supported in Minecraft 1.20.5 or newer versions");

    }

    @Override
    public String getComponentName() {
        return "rarity";
    }

    public static void clear(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt ->{
                nbt.removeKey(MINECRAFT_PREFIX + "rarity");
            });
        }
    }

    public static void remove(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt ->{
                nbt.removeKey(MINECRAFT_PREFIX + "rarity");
                nbt.getOrCreateCompound("!" + MINECRAFT_PREFIX + "rarity");
            });
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(rarity);
    }
}
