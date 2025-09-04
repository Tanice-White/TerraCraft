package io.github.tanice.terraCraft.bukkit.item.component.vanilla;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBTCompoundList;
import io.github.tanice.terraCraft.api.item.component.vanilla.TerraPotionComponent;
import io.github.tanice.terraCraft.bukkit.util.nbtapi.vanilla.NBTPotion;
import io.github.tanice.terraCraft.bukkit.util.version.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.util.version.ServerVersion;
import io.github.tanice.terraCraft.core.util.logger.TerraCraftLogger;
import io.github.tanice.terraCraft.core.util.ColorUtil;
import io.github.tanice.terraCraft.core.util.namespace.TerraNamespaceKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * PotionContents + PotionDurationScale
 */
public class PotionComponent implements TerraPotionComponent {
    @Nullable
    private final Integer color;

    private final List<NBTPotion> potions;
    /* 控制外观*/
    @Nullable
    private final String customName; /* 1.21.2 */
    @Nullable
    private final TerraNamespaceKey id;
    @Nullable
    private final Float durationScale; /* 1.21.5 */

    public PotionComponent(@Nullable Integer color, @Nullable List<NBTPotion> potions, @Nullable String customName, @Nullable TerraNamespaceKey id, @Nullable Float durationScale) {
        this.color = color;
        this.potions = potions == null ? new ArrayList<>() : potions;
        this.customName = customName;
        this.id = id;
        this.durationScale = durationScale;
    }

    public PotionComponent(ConfigurationSection cfg) {
        this(
                ColorUtil.stringToRgb(cfg.getString("color")),
                null,
                cfg.getString("name"),
                TerraNamespaceKey.from(cfg.getString("id")),
                cfg.isSet("duration_scale") ? (float) cfg.getDouble("duration_scale") : null
        );

        ConfigurationSection sub = cfg.getConfigurationSection("potions");
        if (sub != null) {
            for (String key : sub.getKeys(false)) this.potions.add(NBTPotion.from(key, sub.getConfigurationSection(key)));
        }
    }

    @Override
    public void cover(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt ->{
                ReadWriteNBT component = nbt.getOrCreateCompound(MINECRAFT_PREFIX + "potion_contents");
                if (color != null) component.setInteger("custom_color", color);
                if (!potions.isEmpty()) {
                    ReadWriteNBTCompoundList compoundList = component.getCompoundList("custom_effects");
                    /* 覆盖 */
                    compoundList.clear();
                    for (NBTPotion potion : potions) potion.addToCompound(compoundList.addCompound());
                }
                if (customName != null && ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) component.setString("custom_name", customName);
                else TerraCraftLogger.warning("custom name in Potion contents component is only supported in Minecraft 1.21.2 or newer versions");
                if (id != null) component.setString("potion", id.get());
                if (durationScale != null && ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_5)) {
                    nbt.setFloat(MINECRAFT_PREFIX + "potion_duration_scale", durationScale);

                } else TerraCraftLogger.warning("Potion duration scale component is only supported in Minecraft 1.21.5 or newer versions");
            });

        } else TerraCraftLogger.warning("Potion contents component is only supported in Minecraft 1.20.5 or newer versions");
    }

    @Override
    public String getComponentName() {
        return "potion";
    }

    public static void clear(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt ->{
                nbt.removeKey(MINECRAFT_PREFIX + "potion_contents");
                nbt.removeKey(MINECRAFT_PREFIX + "potion_duration_scale");
            });
        }
    }

    public static void remove(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt ->{
                nbt.removeKey(MINECRAFT_PREFIX + "potion_contents");
                nbt.getOrCreateCompound("!" + MINECRAFT_PREFIX + "potion_contents");
                nbt.removeKey(MINECRAFT_PREFIX + "potion_duration_scale");
                nbt.getOrCreateCompound("!" + MINECRAFT_PREFIX + "potion_duration_scale");
            });
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, potions, customName, durationScale, id);
    }
}
