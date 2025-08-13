package io.github.tanice.terraCraft.bukkit.items.components;

import de.tr7zw.nbtapi.NBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.vanilla.TerraDamageResistantComponent;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import io.github.tanice.terraCraft.core.utils.namespace.TerraNamespaceKey;

public class DamageResistantComponent implements TerraDamageResistantComponent {

    private final TerraNamespaceKey resistantType;

    public DamageResistantComponent(TerraNamespaceKey resistantType) {
        this.resistantType = resistantType;
    }

    @Override
    public void apply(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt ->{
                nbt.getOrCreateCompound(COMPONENT_KEY).getOrCreateCompound(MINECRAFT_PREFIX + "damage_resistant").setString("types", "#" + resistantType.get());
            });
        } else TerraCraftLogger.warning("damage resistant component is only supported in Minecraft 1.21.2 or newer versions");
    }
}
