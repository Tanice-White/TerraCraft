package io.github.tanice.terraCraft.bukkit.item.component.vanilla;

import de.tr7zw.nbtapi.NBT;
import io.github.tanice.terraCraft.api.item.component.vanilla.TerraDamageResistantComponent;
import io.github.tanice.terraCraft.bukkit.util.version.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.util.version.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import io.github.tanice.terraCraft.core.util.namespace.TerraNamespaceKey;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class DamageResistantComponent implements TerraDamageResistantComponent {

    private final TerraNamespaceKey resistantType;

    public DamageResistantComponent(TerraNamespaceKey resistantType) {
        this.resistantType = resistantType;
    }

    @Override
    public void cover(ItemStack item) {
        clear(item);
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
            NBT.modifyComponents(item, nbt ->{
                nbt.getOrCreateCompound(MINECRAFT_PREFIX + "damage_resistant").setString("types", "#" + resistantType.get());
            });
        } else TerraCraftLogger.warning("damage resistant component is only supported in Minecraft 1.21.2 or newer versions");
    }

    @Override
    public String getComponentName() {
        return "resistant";
    }

    public static void clear(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
            NBT.modifyComponents(item, nbt ->{
                nbt.removeKey(MINECRAFT_PREFIX + "damage_resistant");
            });
        }
    }

    public static void remove(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
            NBT.modifyComponents(item, nbt ->{
                nbt.removeKey(MINECRAFT_PREFIX + "damage_resistant");
                nbt.getOrCreateCompound("!" + MINECRAFT_PREFIX + "damage_resistant");
            });
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(resistantType);
    }
}
