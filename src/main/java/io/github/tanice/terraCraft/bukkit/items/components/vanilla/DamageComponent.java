package io.github.tanice.terraCraft.bukkit.items.components.vanilla;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.TerraBaseComponent;
import io.github.tanice.terraCraft.api.items.components.vanilla.TerraDamageComponent;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;

import javax.annotation.Nullable;

/**
 * 融合 damage + maxDamage + unbreakable
 */
public class DamageComponent implements TerraDamageComponent {

    @Nullable
    private Integer damage;
    @Nullable
    private final Integer maxDamage;
    @Nullable
    private final Boolean unbreakable;

    public DamageComponent(@Nullable Integer damage, @Nullable Integer maxDamage, @Nullable Boolean unbreakable) {
        this.damage = damage;
        this.maxDamage = maxDamage;
        this.unbreakable = unbreakable;
    }

    @Override
    public void apply(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                ReadWriteNBT component = nbt.getOrCreateCompound(COMPONENT_KEY);
                if (damage != null) component.setInteger(MINECRAFT_PREFIX + "damage", damage);
                if (maxDamage != null) component.setInteger(MINECRAFT_PREFIX + "max_damage", maxDamage);
                if (unbreakable != null && unbreakable) component.getOrCreateCompound(MINECRAFT_PREFIX + "unbreakable");
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt -> {
                if (unbreakable != null && unbreakable) nbt.getOrCreateCompound(TAG_KEY).setBoolean("Unbreakable", true);
                if (damage != null) nbt.getOrCreateCompound(TAG_KEY).setInteger("Damage", damage);
                if (maxDamage != null) TerraCraftLogger.warning("Versions before 1.20.5 do not support setting max_damage. Only damage is configurable. Max damage uses default.");
            });
        }
    }

    @Override
    public void clear(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                ReadWriteNBT component = nbt.getOrCreateCompound(COMPONENT_KEY);
                component.removeKey(MINECRAFT_PREFIX + "damage");
                component.removeKey(MINECRAFT_PREFIX + "max_damage");
                component.removeKey(MINECRAFT_PREFIX + "unbreakable");
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt -> {
                nbt.getOrCreateCompound(TAG_KEY).removeKey("Unbreakable");
                nbt.getOrCreateCompound(TAG_KEY).removeKey("Damage");
            });
        }
    }

    @Override
    public void remove(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                ReadWriteNBT component = nbt.getOrCreateCompound(COMPONENT_KEY);
                component.removeKey(MINECRAFT_PREFIX + "damage");
                component.removeKey(MINECRAFT_PREFIX + "max_damage");
                component.removeKey(MINECRAFT_PREFIX + "unbreakable");
                component.getOrCreateCompound("!" + MINECRAFT_PREFIX + "damage");
                component.getOrCreateCompound("!" + MINECRAFT_PREFIX + "max_damage");
                component.getOrCreateCompound("!" + MINECRAFT_PREFIX + "unbreakable");
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt -> {
                nbt.getOrCreateCompound(TAG_KEY).removeKey("Unbreakable");
                nbt.getOrCreateCompound(TAG_KEY).removeKey("Damage");
            });
        }
    }

    @Override
    public void updatePartialFrom(TerraBaseComponent old) {
        this.damage = ((DamageComponent) old).damage;
    }
}
