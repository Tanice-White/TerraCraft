package io.github.tanice.terraCraft.bukkit.item.component.vanilla;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.github.tanice.terraCraft.api.item.component.vanilla.TerraWeaponComponent;
import io.github.tanice.terraCraft.bukkit.util.version.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.util.version.ServerVersion;
import io.github.tanice.terraCraft.core.util.logger.TerraCraftLogger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Objects;

public class WeaponComponent implements TerraWeaponComponent {
    @Nullable
    private final Float disableBlockingForSeconds;
    @Nullable
    private final Integer itemDamagePerAttack;

    public WeaponComponent(@Nullable Float disableBlockingForSeconds, @Nullable Integer itemDamagePerAttack) {
        this.disableBlockingForSeconds = disableBlockingForSeconds;
        this.itemDamagePerAttack = itemDamagePerAttack;
    }

    public WeaponComponent(ConfigurationSection cfg) {
        this(
                cfg.isSet("disable_shield_seconds") ? (float) cfg.getDouble("disable_shield_seconds") : null,
                cfg.isSet("damage_per_attack") ? cfg.getInt("damage_per_attack") : null
        );
    }

    @Override
    public void cover(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_5)) {
            NBT.modifyComponents(item, nbt -> {
                ReadWriteNBT component = nbt.getOrCreateCompound(MINECRAFT_PREFIX + "weapon");
                if (disableBlockingForSeconds != null) component.setFloat("disable_blocking_for_seconds", disableBlockingForSeconds);
                if (itemDamagePerAttack != null) component.setInteger("item_damage_per_attack", itemDamagePerAttack);
            });
        } else TerraCraftLogger.warning("weapon component is only supported in Minecraft 1.21.5 or newer versions");
    }

    @Override
    public String getComponentName() {
        return "weapon";
    }

    public static void clear(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_5)) {
            NBT.modifyComponents(item, nbt -> {
                nbt.removeKey(MINECRAFT_PREFIX + "weapon");
            });
        }
    }

    public static void remove(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_5)) {
            NBT.modifyComponents(item, nbt -> {
                nbt.removeKey(MINECRAFT_PREFIX + "weapon");
                nbt.getOrCreateCompound("!" + MINECRAFT_PREFIX + "weapon");
            });
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(disableBlockingForSeconds, itemDamagePerAttack);
    }
}
