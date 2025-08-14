package io.github.tanice.terraCraft.bukkit.items.components.vanilla;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBTCompoundList;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.vanilla.TerraToolComponent;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import io.github.tanice.terraCraft.core.utils.namespace.TerraNamespaceKey;

import javax.annotation.Nullable;
import java.util.List;

public class ToolComponent implements TerraToolComponent {
    @Nullable
    private final Boolean canDestroyInCreative;
    @Nullable
    private final Integer damagePerBlock;
    @Nullable
    private final Float defaultMiningSpeed;

    private final List<DigConfig> rules;

    public ToolComponent(@Nullable Boolean canDestroyInCreative, @Nullable Integer damagePerBlock, @Nullable Float defaultMiningSpeed, List<DigConfig> rules) {
        this.canDestroyInCreative = canDestroyInCreative;
        this.damagePerBlock = damagePerBlock;
        this.defaultMiningSpeed = defaultMiningSpeed;
        this.rules = rules;
    }

    @Override
    public void apply(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                ReadWriteNBT component = nbt.getOrCreateCompound(COMPONENT_KEY).getOrCreateCompound(MINECRAFT_PREFIX + "tool");
                if (canDestroyInCreative != null) {
                    if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_5)) component.setBoolean("can_destroy_blocks_in_creative", canDestroyInCreative);
                    else TerraCraftLogger.warning("can_destroy_blocks_in_creative in Tool component is only supported in Minecraft 1.21.5 or newer versions");
                }
                if (damagePerBlock != null) component.setInteger("damage_per_block", damagePerBlock);
                if (defaultMiningSpeed != null) component.setFloat("default_mining_speed", defaultMiningSpeed);
                ReadWriteNBTCompoundList compoundList = component.getCompoundList("rules");
                ReadWriteNBT subCompound;
                for (DigConfig rule : rules) {
                    subCompound = compoundList.addCompound();
                    subCompound.getStringList("blocks").addAll(rule.items().stream().map(TerraNamespaceKey::get).toList());
                    if (rule.speed() != null) subCompound.setFloat("speed", rule.speed());
                    if (rule.correctForDrops() != null) subCompound.setBoolean("correct_for_drops", rule.correctForDrops());
                }
            });
        } else TerraCraftLogger.warning("Tool component is only supported in Minecraft 1.20.5 or newer versions");
    }

    @Override
    public void clear(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.getOrCreateCompound(COMPONENT_KEY).removeKey(MINECRAFT_PREFIX + "tool");
            });
        }
    }

    @Override
    public void remove(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.getOrCreateCompound(COMPONENT_KEY).removeKey(MINECRAFT_PREFIX + "tool");
                nbt.getOrCreateCompound(COMPONENT_KEY).getOrCreateCompound("!" + MINECRAFT_PREFIX + "tool");
            });
        }
    }

    public record DigConfig(List<TerraNamespaceKey> items, @Nullable Boolean correctForDrops, @Nullable Float speed) {
    @Override
    public List<TerraNamespaceKey> items() {
            return this.items;
        }
    }
}
