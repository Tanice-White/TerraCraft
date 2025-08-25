package io.github.tanice.terraCraft.bukkit.item.component.vanilla;

import de.tr7zw.nbtapi.NBT;
import io.github.tanice.terraCraft.api.item.TerraBaseItem;
import io.github.tanice.terraCraft.api.item.component.vanilla.TerraDyedColor;
import io.github.tanice.terraCraft.bukkit.util.version.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.util.version.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import io.github.tanice.terraCraft.core.util.ColorUtil;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Objects;

public class DyedColorComponent implements TerraDyedColor {

    @Nullable
    private final Integer color;

    public DyedColorComponent(@Nullable String color) {
        this.color = ColorUtil.stringToRgb(color);
    }

    @Override
    public void apply(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt -> {
                if (color != null) nbt.setInteger(MINECRAFT_PREFIX + "dyed_color", color);
            });
        } else TerraCraftLogger.warning("Dye color component is only supported in Minecraft 1.20.5 or newer versions");
    }

    @Override
    public String getComponentName() {
        return "dyed_color";
    }

    public static void clear(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.removeKey(MINECRAFT_PREFIX + "dyed_color");
            });
        }
    }

    public static void remove(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.removeKey(MINECRAFT_PREFIX + "dyed_color");
                nbt.getOrCreateCompound("!" + MINECRAFT_PREFIX + "dyed_color");
            });
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(color);
    }
}
