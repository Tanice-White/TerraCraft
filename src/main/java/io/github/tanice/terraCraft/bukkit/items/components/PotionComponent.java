package io.github.tanice.terraCraft.bukkit.items.components;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBTCompoundList;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.TerraPotionComponent;
import io.github.tanice.terraCraft.bukkit.utils.nbtapi.NBTPotion;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;

import java.util.List;

/**
 * PotionContents + PotionDurationScale
 */
public class PotionComponent implements TerraPotionComponent {
    private final Integer color;
    private final List<NBTPotion> potions;
    /* 控制外观*/
    private final String customName; /* 1.21.2 */
    private final String potionId;

    private final Float durationScale; /* 1.21.5 */

    public PotionComponent(Integer color, List<NBTPotion> potions, String customName, String potionId, Float durationScale) {
        this.color = color;
        this.potions = potions;
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
                if (potions != null && !potions.isEmpty()) {
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
}
