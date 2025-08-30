package io.github.tanice.terraCraft.bukkit.item.component;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import io.github.tanice.terraCraft.api.item.TerraItemManager;
import io.github.tanice.terraCraft.api.item.component.*;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.util.version.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.util.version.ServerVersion;
import io.github.tanice.terraCraft.core.util.logger.TerraCraftLogger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static io.github.tanice.terraCraft.api.command.TerraCommand.*;

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

    public GemHolderComponent(ConfigurationSection cfg) {
        super(cfg.getBoolean("updatable", true));
        this.limit = cfg.getInt("limit");
        this.gems = new ArrayList<>();
        TerraItemManager itemManager = TerraCraftBukkit.inst().getItemManager();
        for (String name : cfg.getStringList("gems")) {
            itemManager.getItem(name).ifPresentOrElse(terraItem -> {
                ItemStack item = terraItem.getBukkitItem();
                if (GemComponent.from(item) != null) this.gems.add(item);
                else TerraCraftLogger.error("Item: " + name + " is not a gem");
            }, () -> TerraCraftLogger.warning("Gem: " + name + " dose not exist, or has not been loaded."));
        }
    }

    @Nullable
    public static GemHolderComponent from(ItemStack item) {
        if (item == null || item.isEmpty()) return null;
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            return NBT.getComponents(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".gem_hold");
                if (data == null) return null;
                return fromNBT(data);
            });
        } else {
            return NBT.get(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(TERRA_COMPONENT_KEY + ".gem_hold");
                if (data == null) return null;
                return fromNBT(data);
            });
        }
    }

    @Override
    public void doCover(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt -> {
                ReadWriteNBT data = nbt.resolveOrCreateCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".gem_hold");
                addToCompound(data);
            });
        } else {
            NBT.modify(item, nbt -> {
                ReadWriteNBT data = nbt.resolveOrCreateCompound(TERRA_COMPONENT_KEY + ".gem_hold");
                addToCompound(data);
            });
        }
    }

    @Override
    public void updateLore() {

    }

    public static void clear(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt -> {
                nbt.resolveOrCreateCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY).removeKey("gem_hold");
            });
        } else {
            NBT.modify(item, nbt -> {
                nbt.resolveOrCreateCompound(TERRA_COMPONENT_KEY).removeKey("gem_hold");
            });
        }
    }

    public static void remove(ItemStack item) {
        clear(item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gems, limit);
    }

    @Override
    public String getComponentName() {
        return "gem_holder";
    }

    @Override
    public TerraBaseComponent updatePartial() {
        /* 为null确保继承原本的gem */
        return new GemHolderComponent(this.limit, null, this.state);
    }

    @Override
    public int getGemNums() {
        return gems == null ? 0 : gems.size();
    }

    @Override
    public List<ItemStack> getGems() {
        return gems == null ? new ArrayList<>() : gems;
    }

    @Override
    public void setGems(@Nullable List<ItemStack> gems) {
        if (gems != null) this.gems = gems;
        else this.gems = new ArrayList<>();
    }

    @Override
    public int getLimit() {
        return this.limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    private void addToCompound(ReadWriteNBT compound) {
        if (gems != null && !gems.isEmpty()) {
            compound.getOrCreateCompound("gems").clearNBT();
            compound.getOrCreateCompound("gems").mergeCompound(NBT.itemStackArrayToNBT(gems.toArray(ItemStack[]::new)));
        }
        compound.setInteger("limit", limit);
        compound.setByte("state", state.toNbtByte());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(BOLD).append(YELLOW).append("gem_holder:").append("\n");
        sb.append("    ").append(AQUA).append("limit:").append(WHITE).append(limit).append("\n");
        sb.append("    ").append(AQUA).append("gems:").append(RESET);
        List<ItemStack> gemList = getGems();
        if (gemList.isEmpty()) {
            sb.append(GRAY).append("null");
        } else {
            TerraInnerNameComponent nameComponent;
            for (int i = 0; i < gemList.size(); i++) {
                nameComponent = TerraNameComponent.from(gemList.get(i));
                sb.append(i < limit ? WHITE : GRAY).append(nameComponent == null ? RED + "InvalidTerraGem" : nameComponent.getName()).append(RESET);
                if (i != gemList.size() - 1) sb.append(", ");
            }
        }
        sb.append("\n").append("    ").append(AQUA).append("state:").append(WHITE).append(state).append(RESET);
        return sb.toString();
    }

    private static GemHolderComponent fromNBT(ReadableNBT nbt) {
        int limit = nbt.getInteger("limit");
        ComponentState state = new ComponentState(nbt.getByte("state"));
        List<ItemStack> gs = null;
        ReadableNBT gc = nbt.getCompound("gems");
        if (gc != null) {
            ItemStack[] stacks = NBT.itemStackArrayFromNBT(gc);
            if (stacks == null) TerraCraftLogger.error("Error reading gemHolderComponent");
            else gs = new ArrayList<>(Arrays.stream(stacks).toList());
        }
        return new GemHolderComponent(limit, gs, state);
    }
}
