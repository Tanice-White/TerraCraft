package io.github.tanice.terraCraft.bukkit.item.component.vanilla;

import de.tr7zw.nbtapi.NBT;
import io.github.tanice.terraCraft.api.item.component.vanilla.TerraCustomNameComponent;
import io.github.tanice.terraCraft.bukkit.util.MiniMessageUtil;
import io.github.tanice.terraCraft.bukkit.util.version.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.util.version.ServerVersion;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Objects;

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
    public void cover(ItemStack item) {
        /* 名称的component变化未列出, 经常出莫名其妙的bug, 用meta */
        clear(item);
        if (name == null) return;
        NBT.modify(item, nbt -> {
            nbt.modifyMeta((rNbt, meta) -> {meta.displayName(name);});
        });

    }

    @Override
    public String getComponentName() {
        return "custom_name";
    }

    public static void clear(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt -> {
                nbt.removeKey(MINECRAFT_PREFIX + "custom_name");
                nbt.removeKey(MINECRAFT_PREFIX + "item_name");
            });
        } else {
            NBT.modify(item, nbt -> {
                nbt.getOrCreateCompound("display").removeKey("Name");
            });
        }
    }

    public static void remove(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt -> {
                nbt.getOrCreateCompound("!" + MINECRAFT_PREFIX + "custom_name");
                nbt.getOrCreateCompound("!" + MINECRAFT_PREFIX + "item_name");
            });
        } else {
            NBT.modify(item, nbt -> {
                nbt.getOrCreateCompound("display").removeKey("Name");
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
