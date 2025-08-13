package io.github.tanice.terraCraft.bukkit.items.components;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBTCompoundList;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.vanilla.TerraConsumableComponent;
import io.github.tanice.terraCraft.bukkit.utils.nbtapi.NBTEffect;
import io.github.tanice.terraCraft.bukkit.utils.nbtapi.NBTSound;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;


public class ConsumableComponent implements TerraConsumableComponent {
    @Nullable
    private final Animation animation;
    @Nullable
    private final Float consumeSeconds;
    @Nullable
    private final Boolean hasConsumeParticles;

    private final List<NBTEffect> onConsumeEffects;
    @Nullable
    private final NBTSound sound;

    public ConsumableComponent(@Nullable Animation animation, @Nullable Float consumeSeconds, @Nullable Boolean hasConsumeParticles, @Nullable NBTSound sound) {
        this.animation = animation;
        this.consumeSeconds = consumeSeconds;
        this.hasConsumeParticles = hasConsumeParticles;
        this.onConsumeEffects = new ArrayList<>();
        this.sound = sound;
    }

    @Override
    public void apply(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                ReadWriteNBT component = nbt.getOrCreateCompound(COMPONENT_KEY).getOrCreateCompound(MINECRAFT_PREFIX + "consumable");

                if (animation != null) {
                    if (animation == Animation.BUNDLE) {
                        if (ServerVersion.isBefore(MinecraftVersions.v1_21_4)) {
                            TerraCraftLogger.warning("bundle value in animation is only supported in Minecraft 1.21.4 or newer versions. Use eat as default");
                            component.setString("animation", Animation.EAT.name().toLowerCase());
                        }
                    } else component.setString("animation", animation.name().toLowerCase());
                }
                if (consumeSeconds != null) component.setFloat("consume_seconds", consumeSeconds);
                if (hasConsumeParticles != null) component.setBoolean("has_consume_particles", hasConsumeParticles);

                ReadWriteNBTCompoundList compoundList = component.getCompoundList("on_consume_effects");
                for (NBTEffect ce : onConsumeEffects) ce.addToCompound(compoundList.addCompound());
                if (sound != null) sound.addToCompound(component.getOrCreateCompound("sound"));
            });

        } else TerraCraftLogger.warning("Consumable component is only supported in Minecraft 1.21.2 or newer versions");
    }

    @Override
    public void addEffect(NBTEffect effect) {
        this.onConsumeEffects.add(effect);
    }
}
