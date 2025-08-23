package io.github.tanice.terraCraft.bukkit.items.components.vanilla;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBTCompoundList;
import de.tr7zw.nbtapi.iface.ReadWriteNBTList;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.vanilla.TerraLoreComponent;
import io.github.tanice.terraCraft.bukkit.utils.MiniMessageUtil;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class LoreComponent implements TerraLoreComponent {

    @Nullable
    private final List<Component> lore;

    public LoreComponent(@Nullable List<Component> lore) {
        this.lore = lore;
    }

    @Override
    public void apply(ItemStack item) {
        if (lore == null) return;
        /*  lore经常出莫名其妙的bug, 用meta */
        NBT.modify(item, nbt -> {
            nbt.modifyMeta((rNbt, meta) -> meta.lore(lore));
        });
    }

    @Override
    public String getComponentName() {
        return "lore";
    }

    public static void clear(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt ->{
                nbt.removeKey(MINECRAFT_PREFIX + "lore");
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt ->{
                nbt.getOrCreateCompound("display").removeKey("lore");
            });
        }
    }

    public static void remove(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt ->{
                nbt.removeKey(MINECRAFT_PREFIX + "lore");
                nbt.getOrCreateCompound("!" + MINECRAFT_PREFIX + "lore");
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt ->{
                nbt.getOrCreateCompound("display").removeKey("lore");
            });
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(lore);
    }
}
