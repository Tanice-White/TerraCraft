package io.github.tanice.terraCraft.bukkit.items.components.vanilla;

import de.tr7zw.nbtapi.NBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.vanilla.TerraCustomModelDataComponent;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;

import javax.annotation.Nullable;

public class CustomModelDataComponent implements TerraCustomModelDataComponent {
    @Nullable
    private final Integer cmd;

    public CustomModelDataComponent(@Nullable Integer cmd) {
        this.cmd = cmd;
    }

    @Override
    public void apply(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                if (cmd != null) nbt.getOrCreateCompound(COMPONENT_KEY).getOrCreateCompound(MINECRAFT_PREFIX + "custom_model_data").getFloatList("floats").add(cmd.floatValue());
            });
        } else NBT.modify(item.getBukkitItem(), nbt -> {nbt.getOrCreateCompound(TAG_KEY).setInteger("CustomModelData", cmd);});
    }

    public static void clear(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.getOrCreateCompound(COMPONENT_KEY).removeKey(MINECRAFT_PREFIX + "custom_model_data");
            });
        } else NBT.modify(item.getBukkitItem(), nbt -> {nbt.getOrCreateCompound(TAG_KEY).removeKey("CustomModelData");});
    }

    public static void remove(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.getOrCreateCompound(COMPONENT_KEY).removeKey(MINECRAFT_PREFIX + "custom_model_data");
                nbt.getOrCreateCompound(COMPONENT_KEY).getOrCreateCompound("!" + MINECRAFT_PREFIX + "custom_model_data");
            });
        } else NBT.modify(item.getBukkitItem(), nbt -> {nbt.getOrCreateCompound(TAG_KEY).removeKey("CustomModelData");});
    }
}
