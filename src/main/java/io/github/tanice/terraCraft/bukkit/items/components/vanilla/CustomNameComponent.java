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
    private final Component name;

    public CustomNameComponent(@Nullable String name) {
        this.name = MiniMessageUtil.serialize(name);
    }

    @Override
    public void apply(ItemStack item) {
        /* 名称的component变化未列出, 经常出莫名其妙的bug, 用meta */
        if (name == null) return;
        NBT.modify(item, nbt -> {
            nbt.modifyMeta((rNbt, meta) -> {meta.displayName(name);});
        });

    }

    @Override
    public String getComponentName() {
        return "custom_name";
    }

    public static void clear(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.removeKey(MINECRAFT_PREFIX + "custom_name");
                nbt.removeKey(MINECRAFT_PREFIX + "item_name");
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt -> {
                nbt.getOrCreateCompound("display").removeKey("Name");
            });
        }
    }

    public static void remove(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.getOrCreateCompound("!" + MINECRAFT_PREFIX + "custom_name");
                nbt.getOrCreateCompound("!" + MINECRAFT_PREFIX + "item_name");
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt -> {
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
