package io.github.tanice.terraCraft.bukkit.items.components.vanilla;

import de.tr7zw.nbtapi.NBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.vanilla.TerraEnchantmentGlintOverrideComponent;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Objects;

public class EnchantmentGlintOverrideComponent implements TerraEnchantmentGlintOverrideComponent {
    @Nullable
    private final Boolean glint;

    public EnchantmentGlintOverrideComponent(@Nullable Boolean glint) {
        this.glint = glint;
    }

    @Override
    public void apply(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt -> {
                if(glint != null) nbt.setBoolean(MINECRAFT_PREFIX + "enchantment_glint_override", glint);
            });
        } else TerraCraftLogger.warning("Enchantment glint override component is only supported in Minecraft 1.20.5 or newer versions");
    }

    public static void clear(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.removeKey(MINECRAFT_PREFIX + "enchantment_glint_override");
            });
        }
    }

    public static void remove(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.removeKey(MINECRAFT_PREFIX + "enchantment_glint_override");
                nbt.getOrCreateCompound("!" + MINECRAFT_PREFIX + "enchantment_glint_override");
            });
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(glint);
    }
}
