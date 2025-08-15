package io.github.tanice.terraCraft.bukkit.items.components;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.TerraItemManager;
import io.github.tanice.terraCraft.api.items.components.ComponentState;
import io.github.tanice.terraCraft.api.items.components.TerraBaseComponent;
import io.github.tanice.terraCraft.api.items.components.TerraGemHolderComponent;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.items.AbstractItemComponent;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// TODO + 原版属性
public class GemHolderComponent extends AbstractItemComponent implements TerraGemHolderComponent {

    @Nullable
    private List<ItemStack> gems;
    private int limit;

    public GemHolderComponent(int limit, @Nullable List<String> gemNames, boolean updatable) {
        super(updatable);
        this.limit = limit;
        this.gems = new ArrayList<>(limit);
        if (gemNames != null) {
            TerraItemManager itemManager = TerraCraftBukkit.inst().getItemManager();
            for (String name : gemNames) {
                itemManager.getItem(name).ifPresentOrElse(terraItem -> {
                    ItemStack item = terraItem.getBukkitItem();
                    if (GemComponent.from(item) != null) this.gems.add(item);
                    else TerraCraftLogger.error("Item: " + name + " is not a gem");
                }, () -> TerraCraftLogger.warning("Gem: " + name + " dose not exist, or has not been loaded."));
            }
        }
    }

    public GemHolderComponent(int limit, @Nullable List<ItemStack> gems, ComponentState state) {
        super(state);
        this.limit = limit;
        this.gems = gems;
    }

    @Nullable
    public static GemHolderComponent from(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            return NBT.getComponents(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(COMPONENT_KEY + "." + MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".holds");
                if (data == null) return null;
                return fromNBT(data);
            });
        } else {
            return NBT.get(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(TAG_KEY + "." + TERRA_COMPONENT_KEY + ".holds");
                if (data == null) return null;
                return fromNBT(data);
            });
        }
    }

    @Override
    public void apply(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                ReadWriteNBT data = nbt.resolveOrCreateCompound(COMPONENT_KEY + "." + MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".holds");
                addToCompound(data);
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt -> {
                ReadWriteNBT data = nbt.resolveOrCreateCompound(TAG_KEY + "." + TERRA_COMPONENT_KEY + ".holds");
                addToCompound(data);
            });
        }
    }

    public static void clear(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.resolveOrCreateCompound(COMPONENT_KEY + "." + MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY).removeKey("holds");
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt -> {
                nbt.resolveOrCreateCompound(TAG_KEY + "." + TERRA_COMPONENT_KEY).removeKey("holds");
            });
        }
    }

    public static void remove(TerraBaseItem item) {
        clear(item);
    }

    @Override
    public void updatePartialFrom(TerraBaseComponent old) {
        super.updatePartialFrom(old);
    }

    public @Nullable List<ItemStack> getGems() {
        return this.gems;
    }

    public void setGems(@Nullable List<ItemStack> gems) {
        if (gems != null) this.gems = gems;
        else this.gems = new ArrayList<>();
    }

    public int getLimit() {
        return this.limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    private void addToCompound(ReadWriteNBT compound) {
        if (gems != null && !gems.isEmpty())
            compound.getOrCreateCompound("gems").mergeCompound(NBT.itemStackArrayToNBT(gems.toArray(ItemStack[]::new)));
        compound.setInteger("limit", limit);
    }

    private static GemHolderComponent fromNBT(ReadableNBT nbt) {
        int limit = nbt.getInteger("limit");
        ComponentState state = new ComponentState(nbt.getByte("state"));
        List<ItemStack> gs = null;
        ReadableNBT gc = nbt.getCompound("gems");
        if (gc != null) {
            ItemStack[] stacks = NBT.itemStackArrayFromNBT(gc);
            if (stacks == null) TerraCraftLogger.error("Error reading gemHolderComponent");
            else gs = Arrays.stream(stacks).toList();
        }
        return new GemHolderComponent(limit, gs, state);
    }
}
