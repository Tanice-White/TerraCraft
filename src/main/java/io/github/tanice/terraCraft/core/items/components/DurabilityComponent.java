package io.github.tanice.terraCraft.core.items.components;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.TerraDurabilityComponent;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;

// TODO 支持 weapon 的 攻击耐久减少 和 tool 的挖掘耐久减少
public class DurabilityComponent implements TerraDurabilityComponent {

    private final int damage;
    private final int maxDamage;
    private final boolean lossWhenBreak;

    public DurabilityComponent(int maxDamage, boolean lossWhenBreak) {
        this(maxDamage, maxDamage, lossWhenBreak);
    }

    public DurabilityComponent(int damage, int maxDamage, boolean lossWhenBreak) {
        this.damage = damage;
        this.maxDamage = maxDamage;
        this.lossWhenBreak = lossWhenBreak;
    }

    @Override
    public void apply(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                ReadWriteNBT component = nbt.getOrCreateCompound(COMPONENT_KEY);
                component.getOrCreateCompound(MINECRAFT_PREFIX + "unbreakable");
                /* 操作custom_data部分 */
                ReadWriteNBT data = component.getOrCreateCompound(MINECRAFT_PREFIX + "custom_data");
                data.setInteger(TERRA_PREFIX + "damage", damage);
                data.setInteger(TERRA_PREFIX + "max_damage", maxDamage);
                data.setBoolean(TERRA_PREFIX + "loss_when_break", lossWhenBreak);
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt -> {
                ReadWriteNBT component = nbt.getOrCreateCompound(TAG_KEY);
                component.setBoolean("Unbreakable", true);
                component.setInteger(TERRA_PREFIX + "damage", damage);
                component.setInteger(TERRA_PREFIX + "max_damage", maxDamage);
                component.setBoolean(TERRA_PREFIX + "loss_when_break", lossWhenBreak);
            });
        }
    }
}
