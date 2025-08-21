package io.github.tanice.terraCraft.bukkit.items.components.vanilla;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBTCompoundList;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.vanilla.TerraConsumableComponent;
import io.github.tanice.terraCraft.bukkit.utils.nbtapi.NBTEffect;
import io.github.tanice.terraCraft.bukkit.utils.nbtapi.NBTPotion;
import io.github.tanice.terraCraft.bukkit.utils.nbtapi.NBTSound;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import io.github.tanice.terraCraft.core.utils.namespace.TerraNamespaceKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.github.tanice.terraCraft.core.utils.EnumUtil.safeValueOf;


public class ConsumableComponent implements TerraConsumableComponent {
    @Nullable
    private final Animation animation;
    @Nullable
    private final Float consumeSeconds;
    @Nullable
    private final Boolean hasConsumeParticles;

    private final List<NBTEffect> effects;
    @Nullable
    private final NBTSound sound;

    public ConsumableComponent(@Nullable Animation animation, @Nullable Float consumeSeconds, @Nullable Boolean hasConsumeParticles, @Nullable NBTSound sound) {
        this.animation = animation;
        this.consumeSeconds = consumeSeconds;
        this.hasConsumeParticles = hasConsumeParticles;
        this.effects = new ArrayList<>();
        this.sound = sound;
    }

    public ConsumableComponent(ConfigurationSection cfg) {
        this(
                safeValueOf(Animation.class, cfg.getString("animation"), null),
                cfg.isSet("consume_seconds") ? (float) cfg.getDouble("consume_seconds") : null,
                cfg.isSet("particle") ? cfg.getBoolean("particle") : null,
                new NBTSound(cfg.isSet("sound.range") ? (float) cfg.getDouble("sound.range") : null, TerraNamespaceKey.from(cfg.getString("sound.id")))
        );
        ConfigurationSection sub = cfg.getConfigurationSection("effects");
        if (sub != null) {
            for (String key : sub.getKeys(false)) this.effects.add(NBTEffect.from(key, sub.getConfigurationSection(key)));
        }
    }

    @Override
    public void apply(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
            NBT.modifyComponents(item, nbt -> {
                ReadWriteNBT component = nbt.getOrCreateCompound(MINECRAFT_PREFIX + "consumable");

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
                for (NBTEffect ce : effects) ce.addToCompound(compoundList.addCompound());
                if (sound != null) sound.addToCompound(component.getOrCreateCompound("sound"));
            });

        } else TerraCraftLogger.warning("Consumable component is only supported in Minecraft 1.21.2 or newer versions");
    }

    public static void clear(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.removeKey(MINECRAFT_PREFIX + "consumable");
            });
        }
    }

    public static void remove(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.removeKey(MINECRAFT_PREFIX + "consumable");
                nbt.getOrCreateCompound("!" + MINECRAFT_PREFIX + "consumable");
            });
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(animation, consumeSeconds, hasConsumeParticles, effects, sound);
    }
}
