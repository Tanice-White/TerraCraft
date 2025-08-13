package io.github.tanice.terraCraft.bukkit.items.components;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBTCompoundList;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.vanilla.TerraEnchantComponent;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import io.github.tanice.terraCraft.core.utils.namespace.TerraNamespaceKey;

/**
 * Enchantable + Enchantments + EnchantmentGlintOverride
 */
public class EnchantComponent implements TerraEnchantComponent {

    private final int enchantmentValue; /* Enchantable 1.21.2 加入 */
    private final Boolean glint;
    private final TerraNamespaceKey[] enchantments;
    private final int[] levels;

    public EnchantComponent(int enchantmentValue, Boolean glint, TerraNamespaceKey[] enchantments, int[] levels) {
        this.enchantmentValue = enchantmentValue;
        this.glint = glint;
        this.enchantments = enchantments;
        this.levels = levels;
    }

    @Override
    public void apply(TerraBaseItem item) {
        if (enchantments != null && levels != null && enchantments.length != levels.length) {
            TerraCraftLogger.error("EnchantableComponent: Enchantments and levels arrays must be the same length. " +
                    "Found enchantments length: " + enchantments.length + ", levels length: " + levels.length
            );
            return;
        }

        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                ReadWriteNBT component = nbt.getOrCreateCompound(COMPONENT_KEY);
                if (enchantmentValue >= 0) {
                    if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
                        component.getOrCreateCompound(MINECRAFT_PREFIX + "enchantable").setInteger("value", enchantmentValue);
                    }
                    else TerraCraftLogger.warning("Enchantable component is only supported in Minecraft 1.21.2 or newer versions");
                }
                if(glint != null) component.setBoolean(MINECRAFT_PREFIX + "enchantment_glint_override", glint);

                if (enchantments != null && levels != null) {
                    ReadWriteNBT subCompound = component.getOrCreateCompound(MINECRAFT_PREFIX + "enchantments");
                    for (int i = 0; i < enchantments.length; i++) {
                        subCompound.setInteger(MINECRAFT_PREFIX + enchantments[i], levels[i]);
                    }
                }
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt ->{
                if (enchantments != null && levels != null) {
                    ReadWriteNBTCompoundList compoundList = nbt.getOrCreateCompound(TAG_KEY).getCompoundList("Enchantments");
                    ReadWriteNBT compound;
                    for (int i = 0; i < enchantments.length; i++) {
                        compound = compoundList.addCompound();
                        compound.setString("id", enchantments[i].get());
                        compound.setShort("lvl", (short) levels[i]);
                    }
                }
            });

            if (glint != null && glint) TerraCraftLogger.warning("Enchantment glint override component is only supported in Minecraft 1.20.5 or newer versions");
            if (enchantmentValue >= 0) TerraCraftLogger.warning("Enchantable component is only supported in Minecraft 1.21.2 or newer versions");
        }
    }
}
