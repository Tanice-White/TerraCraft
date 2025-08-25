package io.github.tanice.terraCraft.bukkit.item.component.vanilla;

import de.tr7zw.nbtapi.NBT;
import io.github.tanice.terraCraft.api.item.TerraBaseItem;
import io.github.tanice.terraCraft.api.item.component.vanilla.TerraMaxStackSizeComponent;
import io.github.tanice.terraCraft.bukkit.util.version.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.util.version.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Objects;

public class MaxStackSizeComponent implements TerraMaxStackSizeComponent {
    @Nullable
    private final Integer size;

    public MaxStackSizeComponent(@Nullable Integer size) {
        this.size = size;
    }

    @Override
    public void apply(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt -> {
                if (size != null) nbt.setInteger(MINECRAFT_PREFIX + "max_stack_size", size);
            });
        } else TerraCraftLogger.warning("Modifying max stack size is not supported in this Minecraft version. This feature requires 1.20.5 or later.");
    }

    @Override
    public String getComponentName() {
        return "max_stack_size";
    }

    public static void clear(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.removeKey(MINECRAFT_PREFIX + "max_stack_size");
            });
        }
    }

    public static void remove(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.removeKey(MINECRAFT_PREFIX + "max_stack_size");
                nbt.getOrCreateCompound("!" + MINECRAFT_PREFIX + "max_stack_size");
            });
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(size);
    }
}
