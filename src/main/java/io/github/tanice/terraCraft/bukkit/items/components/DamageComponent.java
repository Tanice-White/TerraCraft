package io.github.tanice.terraCraft.bukkit.items.components;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.TerraDamageComponent;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;

/**
 * 融合 damage + maxDamage + unbreakable
 */
public class DamageComponent implements TerraDamageComponent {

    private final Integer damage;
    private final Integer maxDamage;
    private final Boolean unbreakable;

    public DamageComponent(Integer damage, Integer maxDamage, Boolean unbreakable) {
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
            if (unbreakable != null && unbreakable) NBT.modify(item.getBukkitItem(), nbt -> {nbt.getOrCreateCompound(TAG_KEY).setBoolean("Unbreakable", true);});
            if (damage != null) NBT.modify(item.getBukkitItem(), nbt -> {nbt.getOrCreateCompound(TAG_KEY).setInteger("Damage", damage);});
            if (maxDamage != null) TerraCraftLogger.warning("Versions before 1.20.5 do not support setting max_damage. Only damage is configurable. Max damage uses default.");
        }
    }
}
