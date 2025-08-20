package io.github.tanice.terraCraft.bukkit.items.components.vanilla;

import de.tr7zw.nbtapi.NBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.TerraBaseComponent;
import io.github.tanice.terraCraft.api.items.components.vanilla.TerraDamageComponent;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * 融合 damage + maxDamage + unbreakable
 */
public class DamageComponent implements TerraDamageComponent {

    @Nullable
    private final Integer damage;
    @Nullable
    private final Integer maxDamage;
    @Nullable
    private final Boolean unbreakable;

    public DamageComponent(@Nullable Integer damage, @Nullable Integer maxDamage, @Nullable Boolean unbreakable) {
        this.damage = damage;
        this.maxDamage = maxDamage;
        this.unbreakable = unbreakable;
    }

    public DamageComponent(ConfigurationSection cfg) {
        this(
                cfg.isSet("damage") ? cfg.getInt("damage") : null,
                cfg.isSet("max_damage") ? cfg.getInt("max_damage") : null,
                cfg.isSet("unbreakable") ? cfg.getBoolean("unbreakable") : null
        );
    }

    @Override
    public void apply(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt -> {
                if (damage != null) nbt.setInteger(MINECRAFT_PREFIX + "damage", damage);
                if (maxDamage != null) nbt.setInteger(MINECRAFT_PREFIX + "max_damage", maxDamage);
                if (unbreakable != null && unbreakable) nbt.getOrCreateCompound(MINECRAFT_PREFIX + "unbreakable");
            });
        } else {
            NBT.modify(item, nbt -> {
                if (unbreakable != null && unbreakable) nbt.setBoolean("Unbreakable", true);
                if (damage != null) nbt.setInteger("Damage", damage);
                if (maxDamage != null) TerraCraftLogger.warning("Versions before 1.20.5 do not support setting max_damage. Only damage is configurable. Max damage uses default.");
            });
        }
    }

    public static void clear(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.removeKey(MINECRAFT_PREFIX + "damage");
                nbt.removeKey(MINECRAFT_PREFIX + "max_damage");
                nbt.removeKey(MINECRAFT_PREFIX + "unbreakable");
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt -> {
                nbt.removeKey("Unbreakable");
                nbt.removeKey("Damage");
            });
        }
    }

    public static void remove(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.removeKey(MINECRAFT_PREFIX + "damage");
                nbt.removeKey(MINECRAFT_PREFIX + "max_damage");
                nbt.removeKey(MINECRAFT_PREFIX + "unbreakable");
                nbt.getOrCreateCompound("!" + MINECRAFT_PREFIX + "damage");
                nbt.getOrCreateCompound("!" + MINECRAFT_PREFIX + "max_damage");
                nbt.getOrCreateCompound("!" + MINECRAFT_PREFIX + "unbreakable");
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt -> {
                nbt.removeKey("Unbreakable");
                nbt.removeKey("Damage");
            });
        }
    }

    @Override
    public TerraBaseComponent updatePartial() {
        /* damage 主动设置为 null 不覆盖 */
        return new DamageComponent(null, this.maxDamage, this.unbreakable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(damage, maxDamage, unbreakable);
    }
}
