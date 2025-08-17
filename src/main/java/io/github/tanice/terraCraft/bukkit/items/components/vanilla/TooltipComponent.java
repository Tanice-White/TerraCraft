package io.github.tanice.terraCraft.bukkit.items.components.vanilla;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.vanilla.TerraTooltipComponent;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

import static io.github.tanice.terraCraft.core.utils.EnumUtil.safeValueOf;

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
                cfg.isSet("hide") ? cfg.getStringList("hide") : null,
                cfg.getString("style")
        );
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
            } else {
                if (hideTooltip != null && ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
                    NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                        nbt.resolveOrCreateCompound(COMPONENT_KEY + "." + MINECRAFT_PREFIX + ".hide_tooltip");
                    });
                }
                // TODO 恢复使用NBT(但是NBT更新断层)
                if (hiddenComponents != null) {
                    NBT.modify(item.getBukkitItem(), nbt -> {
                        nbt.modifyMeta((readableNBT, meta) -> {
                            for (String s : hiddenComponents) meta.addItemFlags(safeValueOf(ItemFlag.class, "HIDE_" + s, ItemFlag.HIDE_ARMOR_TRIM));
                        });
                    });
                }
            }
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

    @Override
    public int hashCode() {
        return Objects.hash(hideTooltip, hiddenComponents, tooltipStyle);
    }
}
