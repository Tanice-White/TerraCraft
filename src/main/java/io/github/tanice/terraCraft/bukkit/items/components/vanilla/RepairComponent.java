package io.github.tanice.terraCraft.bukkit.items.components.vanilla;

import de.tr7zw.nbtapi.NBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.vanilla.TerraRepairComponent;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import io.github.tanice.terraCraft.core.utils.namespace.TerraNamespaceKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * Repairable + RepairCost
 */
public class RepairComponent implements TerraRepairComponent {
    @Nullable
    private final Integer cost;
    private final List<TerraNamespaceKey> items;

    public RepairComponent(@Nullable Integer cost, List<TerraNamespaceKey> items) {
        this.cost = cost;
        this.items = items;
    }

    public RepairComponent(ConfigurationSection cfg) {
        this(
                cfg.isSet("cost") ? cfg.getInt("cost") : null,
                cfg.getStringList("items").stream().map(TerraNamespaceKey::from).filter(Objects::nonNull).toList()
        );
    }

    @Override
    public void apply(ItemStack item) {
        if (cost != null) {
            if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
                NBT.modifyComponents(item, nbt -> {
                    nbt.getOrCreateCompound(COMPONENT_KEY).setInteger(MINECRAFT_PREFIX + "repair_cost", cost);
                });
            } else {
                NBT.modify(item, nbt -> {
                    nbt.getOrCreateCompound(TAG_KEY).setInteger("RepairCost", cost);
                });
            }
        }

        if (item != null && !items.isEmpty()) {
            if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
                NBT.modifyComponents(item, nbt -> {
                    nbt.getOrCreateCompound(COMPONENT_KEY)
                            .getOrCreateCompound(MINECRAFT_PREFIX + "repairable")
                            .getStringList("items").addAll(items.stream().map(TerraNamespaceKey::get).toList());
                });
            } else TerraCraftLogger.warning("Repairable component is only supported in Minecraft 1.21.2 or newer versions");
        }
    }

    public static void clear(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.getOrCreateCompound(COMPONENT_KEY).removeKey(MINECRAFT_PREFIX + "repair_cost");
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt -> {
                nbt.getOrCreateCompound(TAG_KEY).removeKey("RepairCost");
            });
        }
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.getOrCreateCompound(COMPONENT_KEY).removeKey(MINECRAFT_PREFIX + "repairable");
            });
        }
    }

    public static void remove(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.getOrCreateCompound(COMPONENT_KEY).removeKey(MINECRAFT_PREFIX + "repair_cost");
                nbt.getOrCreateCompound(COMPONENT_KEY).getOrCreateCompound("!" + MINECRAFT_PREFIX + "repair_cost");
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt -> {
                nbt.getOrCreateCompound(TAG_KEY).removeKey("RepairCost");
            });
        }
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.getOrCreateCompound(COMPONENT_KEY).removeKey(MINECRAFT_PREFIX + "repairable");
                /* 不能禁止武器合武器式修复 */
                nbt.getOrCreateCompound(COMPONENT_KEY).getOrCreateCompound("!" + MINECRAFT_PREFIX + "repairable");
            });
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(cost, items);
    }
}
