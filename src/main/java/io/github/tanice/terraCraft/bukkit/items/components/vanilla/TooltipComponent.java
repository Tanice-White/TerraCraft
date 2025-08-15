package io.github.tanice.terraCraft.bukkit.items.components.vanilla;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.vanilla.TerraTooltipComponent;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;

import javax.annotation.Nullable;
import java.util.List;

/**
 * TooltipDisplay + TooltipStyle
 */
public class TooltipComponent implements TerraTooltipComponent {
    @Nullable
    private final Boolean hideTooltip; /* 1.21.5 */
    @Nullable
    private final List<String> hiddenComponents; /* 1.21.5 */
    @Nullable
    private final String tooltipStyle; /* 1.21.2 */

    public TooltipComponent(@Nullable Boolean hideTooltip, @Nullable List<String> hiddenComponents, @Nullable String tooltipStyle) {
        this.hideTooltip = hideTooltip;
        this.hiddenComponents = hiddenComponents;
        this.tooltipStyle = tooltipStyle;
    }

    @Override
    public void apply(TerraBaseItem item) {
        if (hideTooltip != null || hiddenComponents != null) {
            if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_5)) {
                NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                    ReadWriteNBT component = nbt.getOrCreateCompound(COMPONENT_KEY).getOrCreateCompound(MINECRAFT_PREFIX + "tooltip_display");
                    if (hideTooltip != null) component.setBoolean("hide_tooltip", hideTooltip);
                    if (hiddenComponents != null) component.getStringList("hidden_components").addAll(hiddenComponents);
                });
            } else TerraCraftLogger.warning("Tooltip display component is only supported in Minecraft 1.20.5 or newer versions");
        }

        if (tooltipStyle != null && ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.getOrCreateCompound(COMPONENT_KEY).setString(MINECRAFT_PREFIX + "tooltip_style", tooltipStyle);
            });
        } else TerraCraftLogger.warning("Tooltip style component is only supported in Minecraft 1.21.2 or newer versions");
    }

    public static void clear(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.getOrCreateCompound(COMPONENT_KEY).removeKey(MINECRAFT_PREFIX + "tooltip_display");
            });
        }
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.getOrCreateCompound(COMPONENT_KEY).removeKey(MINECRAFT_PREFIX + "tooltip_style");
            });
        }
    }

    public static void remove(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.getOrCreateCompound(COMPONENT_KEY).removeKey(MINECRAFT_PREFIX + "tooltip_display");
                nbt.getOrCreateCompound(COMPONENT_KEY).getOrCreateCompound("!" + MINECRAFT_PREFIX + "tooltip_display");
            });
        }
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.getOrCreateCompound(COMPONENT_KEY).removeKey(MINECRAFT_PREFIX + "tooltip_style");
                nbt.getOrCreateCompound(COMPONENT_KEY).getOrCreateCompound("!" + MINECRAFT_PREFIX + "tooltip_style");
            });
        }
    }
}
