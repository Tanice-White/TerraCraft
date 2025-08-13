package io.github.tanice.terraCraft.bukkit.items.components;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBTCompoundList;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.vanilla.TerraToolComponent;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import io.github.tanice.terraCraft.core.utils.namespace.TerraNamespaceKey;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class ToolComponent implements TerraToolComponent {

    private final Boolean canDestroyInCreative;
    private final Integer damagePerBlock;
    private final Float defaultMiningSpeed;
    @Nonnull
    private final DigConfig[] rules;

    public ToolComponent(Boolean canDestroyInCreative, Integer damagePerBlock, Float defaultMiningSpeed, DigConfig[] rules) {
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
                    subCompound.getStringList("blocks").addAll(Arrays.stream(rule.items()).map(TerraNamespaceKey::get).toList());
                    if (rule.speed() != null) subCompound.setFloat("speed", rule.speed());
                    if (rule.correctForDrops() != null) subCompound.setBoolean("correct_for_drops", rule.correctForDrops());
                }
            });
        } else TerraCraftLogger.warning("Tool component is only supported in Minecraft 1.20.5 or newer versions");
    }

    public record DigConfig(@Nonnull TerraNamespaceKey[] items, Boolean correctForDrops, Float speed) {
    @Override
    public TerraNamespaceKey[] items() {
            return this.items;
        }
    }
}
