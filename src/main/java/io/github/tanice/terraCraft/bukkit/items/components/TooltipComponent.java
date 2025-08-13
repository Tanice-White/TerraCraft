package io.github.tanice.terraCraft.bukkit.items.components;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.TerraTooltipComponent;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;

import java.util.Arrays;

/**
 * TooltipDisplay + TooltipStyle
 */
public class TooltipComponent implements TerraTooltipComponent {

    private final Boolean hideTooltip; /* 1.21.5 */
    private final String[] hiddenComponents; /* 1.21.5 */
    private final String tooltipStyle; /* 1.21.2 */

    public TooltipComponent(Boolean hideTooltip, String[] hiddenComponents, String tooltipStyle) {
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
                    if (hiddenComponents != null) component.getStringList("hidden_components").addAll(Arrays.asList(hiddenComponents));
                });
            } else TerraCraftLogger.warning("Tooltip display component is only supported in Minecraft 1.20.5 or newer versions");
        }

        if (tooltipStyle != null && ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.getOrCreateCompound(COMPONENT_KEY).setString(MINECRAFT_PREFIX + "tooltip_style", tooltipStyle);
            });
        } else TerraCraftLogger.warning("Tooltip style component is only supported in Minecraft 1.21.2 or newer versions");
    }
}
