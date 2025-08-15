package io.github.tanice.terraCraft.bukkit.items.components.vanilla;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBTCompoundList;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.vanilla.TerraPotionComponent;
import io.github.tanice.terraCraft.bukkit.utils.nbtapi.NBTPotion;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

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
    private final String potionId;
    @Nullable
    private final Float durationScale; /* 1.21.5 */

    public PotionComponent(@Nullable Integer color, @Nullable String customName, @Nullable String potionId, @Nullable Float durationScale) {
        this.color = color;
        this.potions = new ArrayList<>();
        this.customName = customName;
        this.potionId = potionId;
        this.durationScale = durationScale;
    }

    @Override
    public void apply(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt ->{
                ReadWriteNBT component = nbt.getOrCreateCompound(COMPONENT_KEY).getOrCreateCompound(MINECRAFT_PREFIX + "potion_contents");
                if (color != null) component.setInteger("custom_color", color);
                if (!potions.isEmpty()) {
                    ReadWriteNBTCompoundList compoundList = component.getCompoundList("custom_effects");
                    for (NBTPotion potion : potions) potion.addToCompound(compoundList.addCompound());
                }
                if (customName != null && ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) component.setString("custom_name", customName);
                else TerraCraftLogger.warning("custom name in Potion contents component is only supported in Minecraft 1.21.2 or newer versions");
                if (potionId != null) component.setString("potion", potionId);
                if (durationScale != null && ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_5)) {
                    nbt.getOrCreateCompound(COMPONENT_KEY).setFloat(MINECRAFT_PREFIX + "potion_duration_scale", durationScale);

                } else TerraCraftLogger.warning("Potion duration scale component is only supported in Minecraft 1.21.5 or newer versions");
            });

        } else TerraCraftLogger.warning("Potion contents component is only supported in Minecraft 1.20.5 or newer versions");
    }

    public static void clear(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt ->{
                nbt.getOrCreateCompound(COMPONENT_KEY).removeKey(MINECRAFT_PREFIX + "potion_contents");
                nbt.getOrCreateCompound(COMPONENT_KEY).removeKey(MINECRAFT_PREFIX + "potion_duration_scale");
            });
        }
    }

    public static void remove(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt ->{
                nbt.getOrCreateCompound(COMPONENT_KEY).removeKey(MINECRAFT_PREFIX + "potion_contents");
                nbt.getOrCreateCompound(COMPONENT_KEY).getOrCreateCompound("!" + MINECRAFT_PREFIX + "potion_contents");
                nbt.getOrCreateCompound(COMPONENT_KEY).removeKey(MINECRAFT_PREFIX + "potion_duration_scale");
                nbt.getOrCreateCompound(COMPONENT_KEY).getOrCreateCompound("!" + MINECRAFT_PREFIX + "potion_duration_scale");
            });
        }
    }

    public void addPotion(NBTPotion potion) {
        this.potions.add(potion);
    }

    public void addPotions(List<NBTPotion> potions) {
        this.potions.addAll(potions);
    }
}
