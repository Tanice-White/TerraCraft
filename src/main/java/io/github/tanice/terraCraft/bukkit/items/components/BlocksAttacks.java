package io.github.tanice.terraCraft.bukkit.items.components;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.TerraBlocksAttacks;
import io.github.tanice.terraCraft.bukkit.utils.adapter.BukkitDamageTags;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import io.lumine.mythic.api.skills.damage.DamageTags;

public class BlocksAttacks implements TerraBlocksAttacks {

    private float blockDelaySeconds;
    private BukkitDamageTags canPass;
    private

    @Override
    public void apply(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                ReadWriteNBT component = nbt.getOrCreateCompound("components").getOrCreateCompound(MINECRAFT_PREFIX + "blocks_attacks");
            });
        } else {

        }
    }
}
