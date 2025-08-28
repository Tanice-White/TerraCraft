package io.github.tanice.terraCraft.bukkit.item.component.vanilla;

import de.tr7zw.nbtapi.NBT;
import io.github.tanice.terraCraft.api.item.component.vanilla.TerraCustomModelDataComponent;
import io.github.tanice.terraCraft.bukkit.util.version.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.util.version.ServerVersion;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Objects;

public class CustomModelDataComponent implements TerraCustomModelDataComponent {
    @Nullable
    private final Integer cmd;

    public CustomModelDataComponent(@Nullable Integer cmd) {
        this.cmd = cmd;
    }

    @Override
    public void cover(ItemStack item) {
        clear(item);
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt -> {
                if (cmd != null) nbt.getOrCreateCompound(MINECRAFT_PREFIX + "custom_model_data").getFloatList("floats").add(cmd.floatValue());
            });
        } else NBT.modify(item, nbt -> {nbt.setInteger("CustomModelData", cmd);});
    }

    @Override
    public String getComponentName() {
        return "custom_model_data";
    }

    public static void clear(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt -> {
                nbt.removeKey(MINECRAFT_PREFIX + "custom_model_data");
            });
        } else NBT.modify(item, nbt -> {nbt.removeKey("CustomModelData");});
    }

    public static void remove(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt -> {
                nbt.removeKey(MINECRAFT_PREFIX + "custom_model_data");
                nbt.getOrCreateCompound("!" + MINECRAFT_PREFIX + "custom_model_data");
            });
        } else NBT.modify(item, nbt -> {nbt.removeKey("CustomModelData");});
    }

    @Override
    public int hashCode() {
        return Objects.hash(cmd);
    }
}
