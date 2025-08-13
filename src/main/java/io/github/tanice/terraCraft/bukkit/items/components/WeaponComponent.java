package io.github.tanice.terraCraft.bukkit.items.components;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.TerraWeaponComponent;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;

public class WeaponComponent implements TerraWeaponComponent {

    private final Float disableBlockingForSeconds;
    private final Integer itemDamagePerAttack;

    public WeaponComponent(Float disableBlockingForSeconds, Integer itemDamagePerAttack) {
        this.disableBlockingForSeconds = disableBlockingForSeconds;
        this.itemDamagePerAttack = itemDamagePerAttack;
    }

    @Override
    public void apply(TerraBaseItem item) {
        NBT.modifyComponents(item.getBukkitItem(), nbt -> {
            ReadWriteNBT component = nbt.getOrCreateCompound(COMPONENT_KEY).getOrCreateCompound(MINECRAFT_PREFIX + "weapon");
            if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_5)) {
                if (disableBlockingForSeconds != null) component.setFloat("disable_blocking_for_seconds", disableBlockingForSeconds);
                if (itemDamagePerAttack != null) component.setInteger("damage_per_attack", itemDamagePerAttack);

            } else if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
                if (disableBlockingForSeconds != null) component.setBoolean("can_disable_blocking", true);
                if (itemDamagePerAttack != null) component.setInteger("item_damage_per_attack", itemDamagePerAttack);
            } else TerraCraftLogger.warning("weapon component is only supported in Minecraft 1.20.5 or newer versions");
        });
    }
}
