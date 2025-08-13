package io.github.tanice.terraCraft.bukkit.items.components;

import de.tr7zw.nbtapi.NBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.vanilla.TerraCustomNameComponent;
import io.github.tanice.terraCraft.bukkit.utils.MiniMessageUtil;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import net.kyori.adventure.text.Component;

import javax.annotation.Nullable;

/**
 * CustomName + ItemName
 */
public class CustomNameComponent implements TerraCustomNameComponent {

    @Nullable
    private final Component name;

    public CustomNameComponent(@Nullable String name) {
        this.name = MiniMessageUtil.serialize(name);
    }

    @Override
    public void apply(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                if (name != null) {
                    nbt.getOrCreateCompound(COMPONENT_KEY).setString(MINECRAFT_PREFIX + "custom_name", MiniMessageUtil.toNBTJson(name));
                    nbt.getOrCreateCompound(COMPONENT_KEY).setString(MINECRAFT_PREFIX + "item_name", MiniMessageUtil.toNBTJson(name));
                }
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt -> {
                if (name != null) nbt.getOrCreateCompound(TAG_KEY).getOrCreateCompound("display").setString("Name", MiniMessageUtil.toNBTJson(name));
            });
        }
    }
}
