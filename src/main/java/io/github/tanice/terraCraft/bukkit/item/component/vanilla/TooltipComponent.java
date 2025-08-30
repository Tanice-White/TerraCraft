package io.github.tanice.terraCraft.bukkit.item.component.vanilla;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.github.tanice.terraCraft.api.item.component.vanilla.TerraTooltipComponent;
import io.github.tanice.terraCraft.bukkit.util.version.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.util.version.ServerVersion;
import io.github.tanice.terraCraft.core.util.logger.TerraCraftLogger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

import static io.github.tanice.terraCraft.core.util.EnumUtil.safeValueOf;

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

    public TooltipComponent(ConfigurationSection cfg) {
        this(
                cfg.isSet("hide_tooltip") ? cfg.getBoolean("hide_tooltip") : null,
                cfg.isSet("hide_tags") ? cfg.getStringList("hide_tags") : null,
                cfg.getString("style")
        );
    }

    @Override
    public void cover(ItemStack item) {
        if (hideTooltip != null || hiddenComponents != null) {
            if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_5)) {
                NBT.modifyComponents(item, nbt -> {
                    ReadWriteNBT component = nbt.getOrCreateCompound(MINECRAFT_PREFIX + "tooltip_display");
                    if (hideTooltip != null) component.setBoolean("hide_tooltip", hideTooltip);
                    if (hiddenComponents != null) {
                        /* 覆盖 */
                        component.getStringList("hidden_components").clear();
                        component.getStringList("hidden_components").addAll(hiddenComponents);
                    }
                });
            } else {
                /* 1.20.5 - 1.21.4 */
                if (hideTooltip != null && hideTooltip && ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
                    NBT.modifyComponents(item, nbt -> {
                        nbt.getOrCreateCompound(MINECRAFT_PREFIX + "hide_tooltip");
                    });
                } else TerraCraftLogger.warning("Hide tooltip component is only supported in Minecraft 1.20.5 or newer versions");
                /* 1.20.5 - 1.21.4 之间不明, 使用meta */
                if (hiddenComponents != null) {
                    NBT.modify(item, nbt -> {
                        nbt.modifyMeta((readableNBT, meta) -> {
                            meta.removeItemFlags(meta.getItemFlags().toArray(ItemFlag[]::new));
                            for (String s : hiddenComponents) meta.addItemFlags(safeValueOf(ItemFlag.class, "HIDE_" + s, ItemFlag.HIDE_DYE));
                        });
                    });
                }
            }
        }
        if (tooltipStyle != null) {
            if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
                NBT.modifyComponents(item, nbt -> {
                    nbt.setString(MINECRAFT_PREFIX + "tooltip_style", tooltipStyle);
                });
            } else TerraCraftLogger.warning("Tooltip style component is only supported in Minecraft 1.21.2 or newer versions");
        }
    }

    @Override
    public String getComponentName() {
        return "tooltip";
    }

    public static void clear(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_5)) {
            NBT.modifyComponents(item, nbt -> {
                nbt.removeKey(MINECRAFT_PREFIX + "tooltip_display");
            });
        }
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
            NBT.modifyComponents(item, nbt -> {
                nbt.removeKey(MINECRAFT_PREFIX + "tooltip_style");
            });
        }
    }

    public static void remove(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_5)) {
            NBT.modifyComponents(item, nbt -> {
                nbt.removeKey(MINECRAFT_PREFIX + "tooltip_display");
                nbt.getOrCreateCompound("!" + MINECRAFT_PREFIX + "tooltip_display");
            });
        }
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
            NBT.modifyComponents(item, nbt -> {
                nbt.removeKey(MINECRAFT_PREFIX + "tooltip_style");
                nbt.getOrCreateCompound("!" + MINECRAFT_PREFIX + "tooltip_style");
            });
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(hideTooltip, hiddenComponents, tooltipStyle);
    }
}
