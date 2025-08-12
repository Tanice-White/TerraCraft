package io.github.tanice.terraCraft.bukkit.items.components;

import de.tr7zw.nbtapi.NBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.TerraCustomModelDataComponent;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;

public class CustomModelDataComponent implements TerraCustomModelDataComponent {

    private final int cmd;

    public CustomModelDataComponent(int cmd) {
        this.cmd = cmd;
    }

    @Override
    public void apply(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.getOrCreateCompound(COMPONENT_KEY).getOrCreateCompound(MINECRAFT_PREFIX + "custom_model_data").getFloatList("floats").add((float) cmd);
            });

        } else NBT.modify(item.getBukkitItem(), nbt -> {nbt.getOrCreateCompound(TAG_KEY).setInteger("CustomModelData", cmd);});

    }
}
