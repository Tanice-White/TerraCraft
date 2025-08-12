package io.github.tanice.terraCraft.bukkit.items.components;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.TerraDurabilityComponent;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import io.github.tanice.terraCraft.core.utils.namespace.TerraNamespaceKey;

/**
 * 融合 damage + maxDamage + unbreakable
 */
public class DurabilityComponent implements TerraDurabilityComponent {

    private final Integer damage;
    private final Integer maxDamage;
    private final Boolean unbreakable;
    /** 是否是本插件自定义耐久 */
    private final boolean isTerraDamage;

    public DurabilityComponent(Integer damage, Integer maxDamage, Boolean unbreakable, boolean isTerraDamage) {
        this.damage = damage;
        this.maxDamage = maxDamage;
        this.unbreakable = unbreakable;
        this.isTerraDamage = isTerraDamage;
    }

    @Override
    public void apply(TerraBaseItem item) {

        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                ReadWriteNBT component = nbt.getOrCreateCompound(COMPONENT_KEY);
                if (damage != null) component.setInteger(MINECRAFT_PREFIX + "damage", damage);
                if (maxDamage != null) component.setInteger(MINECRAFT_PREFIX + "max_damage", maxDamage);
                if (isTerraDamage || (unbreakable != null && unbreakable)) component.getOrCreateCompound(MINECRAFT_PREFIX + "unbreakable");
            });
        } else {
            if (isTerraDamage || (unbreakable != null && unbreakable)) NBT.modify(item.getBukkitItem(), nbt -> {nbt.getOrCreateCompound(TAG_KEY).setBoolean("Unbreakable", true);});
            if (isTerraDamage) {
                NBT.modify(item.getBukkitItem(), nbt -> {
                    if (damage != null) nbt.getOrCreateCompound(TAG_KEY).setInteger(new TerraNamespaceKey("damage").get(), damage);
                    if (maxDamage != null) nbt.getOrCreateCompound(TAG_KEY).setInteger(new TerraNamespaceKey("max_damage").get(), maxDamage);
                });
            }
            /* 原版耐久 */
            else {
                if (damage != null) NBT.modify(item.getBukkitItem(), nbt -> {nbt.getOrCreateCompound(TAG_KEY).setInteger("Damage", damage);});
                if (maxDamage != null) TerraCraftLogger.warning("Versions before 1.20.5 do not support setting max_damage. Only damage is configurable. Max damage uses default.");
            }
        }
    }
}
