package io.github.tanice.terraCraft.bukkit.items.components.vanilla;

import de.tr7zw.nbtapi.NBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.vanilla.TerraCustomNameComponent;
import io.github.tanice.terraCraft.bukkit.utils.MiniMessageUtil;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * CustomName + ItemName
 */
public class CustomNameComponent implements TerraCustomNameComponent {

    @Nullable
    private Component name;

    public CustomNameComponent(@Nullable String name) {
        this.name = MiniMessageUtil.serialize(name);
    }

    @Override
    public void apply(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt -> {
                if (name != null) {
                    nbt.getOrCreateCompound(COMPONENT_KEY).setString(MINECRAFT_PREFIX + "custom_name", MiniMessageUtil.toNBTJson(name));
                    nbt.getOrCreateCompound(COMPONENT_KEY).setString(MINECRAFT_PREFIX + "item_name", MiniMessageUtil.toNBTJson(name));
                }
            });
        } else {
            NBT.modify(item, nbt -> {
                if (name != null) nbt.getOrCreateCompound(TAG_KEY).getOrCreateCompound("display").setString("Name", MiniMessageUtil.toNBTJson(name));
            });
        }
    }

    public static void clear(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.getOrCreateCompound(COMPONENT_KEY).removeKey(MINECRAFT_PREFIX + "custom_name");
                nbt.getOrCreateCompound(COMPONENT_KEY).removeKey(MINECRAFT_PREFIX + "item_name");
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt -> {
                nbt.getOrCreateCompound(TAG_KEY).getOrCreateCompound("display").removeKey("Name");
            });
        }
    }

    public static void remove(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.getOrCreateCompound(COMPONENT_KEY).getOrCreateCompound("!" + MINECRAFT_PREFIX + "custom_name");
                nbt.getOrCreateCompound(COMPONENT_KEY).getOrCreateCompound("!" + MINECRAFT_PREFIX + "item_name");
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt -> {
                nbt.getOrCreateCompound(TAG_KEY).getOrCreateCompound("display").removeKey("Name");
            });
        }
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
