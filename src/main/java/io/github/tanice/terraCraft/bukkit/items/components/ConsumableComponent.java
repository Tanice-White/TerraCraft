package io.github.tanice.terraCraft.bukkit.items.components;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBTCompoundList;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.TerraConsumableComponent;
import io.github.tanice.terraCraft.bukkit.utils.nbtapi.NBTEffect;
import io.github.tanice.terraCraft.bukkit.utils.nbtapi.NBTSound;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;

import java.util.ArrayList;
import java.util.List;


public class ConsumableComponent implements TerraConsumableComponent {

    private final Animation animation;
    private final float consumeSeconds;
    private final boolean hasConsumeParticles;
    private final List<NBTEffect> onConsumeEffects;
    private final NBTSound sound;

    public ConsumableComponent(Animation animation, float consumeSeconds, boolean hasConsumeParticles, NBTSound sound) {
        this.animation = animation;
        this.consumeSeconds = consumeSeconds;
        this. hasConsumeParticles = hasConsumeParticles;
        this.onConsumeEffects = new ArrayList<>();
        this.sound = sound;
    }

    @Override
    public void apply(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                ReadWriteNBT component = nbt.getOrCreateCompound(COMPONENT_KEY).getOrCreateCompound(MINECRAFT_PREFIX + "consumable");
                component.setString("animation", animation.name().toLowerCase());
                component.setFloat("consume_seconds", consumeSeconds);
                component.setBoolean("has_consume_particles", hasConsumeParticles);

                ReadWriteNBTCompoundList compoundList = component.getCompoundList("on_consume_effects");
                for (NBTEffect ce : onConsumeEffects) {
                    ce.addToCompound(compoundList.addCompound());
                }
                ReadWriteNBT sComponent;
                sComponent = component.getOrCreateCompound("sound");
                sComponent.setFloat("range", sound.getRange());
                sComponent.setString("sound_id", sound.getId());
            });

        } else TerraCraftLogger.warning("Consumable component is only supported in Minecraft 1.21.2 or newer versions");
    }

    @Override
    public void addEffect(NBTEffect effect) {
        this.onConsumeEffects.add(effect);
    }
}
