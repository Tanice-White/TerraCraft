package io.github.tanice.terraCraft.bukkit.items.components.vanilla;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBTCompoundList;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.vanilla.TerraDeathProtectionComponent;
import io.github.tanice.terraCraft.bukkit.utils.nbtapi.NBTEffect;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;

import java.util.ArrayList;
import java.util.List;

public class DeathProtectionComponent implements TerraDeathProtectionComponent {

    private final List<NBTEffect> effects;

    public DeathProtectionComponent() {
        this.effects = new ArrayList<>();
    }

    @Override
    public void apply(TerraBaseItem item) {
        if (effects.isEmpty()) return;
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt ->{
                if (effects.isEmpty()) return;
                ReadWriteNBTCompoundList compoundList = nbt.getOrCreateCompound(COMPONENT_KEY).getOrCreateCompound(MINECRAFT_PREFIX + "death_protection").getCompoundList("death_effects");
                ReadWriteNBT component;
                for (NBTEffect effect : effects) {
                    component = compoundList.addCompound();
                    effect.addToCompound(component);
                }
            });
        } else TerraCraftLogger.warning("death protection component is only supported in Minecraft 1.21.2 or newer versions");
    }

    @Override
    public void clear(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt ->{
                nbt.getOrCreateCompound(COMPONENT_KEY).removeKey(MINECRAFT_PREFIX + "death_protection");
            });
        }
    }

    @Override
    public void remove(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt ->{
                nbt.getOrCreateCompound(COMPONENT_KEY).removeKey(MINECRAFT_PREFIX + "death_protection");
                nbt.getOrCreateCompound(COMPONENT_KEY).getOrCreateCompound("!" + MINECRAFT_PREFIX + "death_protection");
            });
        }
    }


    public void addEffect(NBTEffect effect) {
        this.effects.add(effect);
    }

    public void addEffects(List<NBTEffect> effects) {
        this.effects.addAll(effects);
    }
}
