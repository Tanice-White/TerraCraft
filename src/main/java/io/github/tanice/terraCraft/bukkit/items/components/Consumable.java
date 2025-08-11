package io.github.tanice.terraCraft.bukkit.items.components;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBTCompoundList;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.TerraConsumable;
import io.github.tanice.terraCraft.bukkit.utils.adapter.BukkitEffect;
import io.github.tanice.terraCraft.bukkit.utils.adapter.BukkitSound;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Consumable implements TerraConsumable {

    private final Animation animation;
    private final float consumeSeconds;
    private final boolean hasConsumeParticles;
    private final List<ConsumeEffects> onConsumeEffects;
    private final BukkitSound sound;

    public Consumable(Animation animation, float consumeSeconds, boolean hasConsumeParticles, BukkitSound sound) {
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
                ReadWriteNBT sComponent, ssComponent;
                for (ConsumeEffects ce : onConsumeEffects) {
                    sComponent = compoundList.addCompound();
                    sComponent.setString("type", ce.type);
                    sComponent.setFloat("probability", ce.probability);

                    switch (ce.type) {
                        case "apply_effects" -> {
                            for (BukkitEffect e : ce.effects) {
                                ssComponent = sComponent.getCompoundList("effects").addCompound();
                                addEffectToNBT(ssComponent, e, true);
                            }
                        }
                        case "clear_all_effects" -> sComponent.getCompoundList("effects").clear();
                        case "play_sound" -> {
                            ssComponent = sComponent.getOrCreateCompound("sound");
                            ssComponent.setFloat("range", ce.sound.getRange());
                            ssComponent.setString("sound_id", ce.sound.getId());
                        }
                        case "remove_effects" -> sComponent.getStringList("effects").addAll(Arrays.stream(ce.effectsToBeRemoved).toList());
                        case "teleport_randomly" -> sComponent.setFloat("diameter", ce.diameter);
                    }
                }

                sComponent = component.getOrCreateCompound("sound");
                sComponent.setFloat("range", sound.getRange());
                sComponent.setString("sound_id", sound.getId());
            });

        } else TerraCraftLogger.error("Consumable component is only supported in Minecraft 1.21.5 or newer versions");
    }

    public void addConsumeEffect(float probability, BukkitEffect... effects) {
        this.onConsumeEffects.add(new ConsumeEffects(probability, effects));
    }

    public void addConsumeEffect(float probability, BukkitSound sound) {
        this.onConsumeEffects.add(new ConsumeEffects(probability, sound));
    }

    public void addConsumeEffect(float probability, String... effectsToBeRemoved) {
        this.onConsumeEffects.add(new ConsumeEffects(probability, effectsToBeRemoved));
    }

    /**
     * 移除所有药水效果
     */
    public void addConsumeEffect(float probability) {
        this.onConsumeEffects.add(new ConsumeEffects(probability));
    }

    public void addConsumeEffect(float probability, float diameter) {
        this.onConsumeEffects.add(new ConsumeEffects(probability, diameter));
    }

    /**
     * 递归添加效果到NBT
     * @param targetNBT 目标NBT复合标签
     * @param effect 要添加的效果
     * @param includeId 是否包含id属性（顶层效果包含，递归的子效果不包含）
     */
    private void addEffectToNBT(ReadWriteNBT targetNBT, BukkitEffect effect, boolean includeId) {
        if (includeId) targetNBT.setString("id", effect.getId());

        targetNBT.setBoolean("ambient", effect.isAmbient());
        targetNBT.setByte("amplifier", effect.getAmplifierAsByte());
        targetNBT.setInteger("duration", effect.getDuration());
        targetNBT.setBoolean("show_icon", effect.isShowIcon());
        targetNBT.setBoolean("show_particles", effect.isShowParticles());

        // 处理隐藏效果
        BukkitEffect hiddenEffect = effect.getHiddenEffect();
        if (hiddenEffect != null) {
            ReadWriteNBT hiddenNBT = targetNBT.getOrCreateCompound("hidden_effect");
            // 递归调用，子效果不包含id
            addEffectToNBT(hiddenNBT, hiddenEffect, false);
        }
    }

    /** 内部效果类 */
    private static class ConsumeEffects {
        private final String type;
        private final float probability;
        private BukkitEffect[] effects;
        private BukkitSound sound;
        private String[] effectsToBeRemoved;
        private float diameter;

        public ConsumeEffects(float probability, BukkitEffect... effects) {
            this.type = "apply_effects";
            this.probability = probability;
            this.effects = effects;
        }

        public ConsumeEffects(float probability, BukkitSound sound) {
            this.type = "play_sound";
            this.probability = probability;
            this.sound = sound;
        }

        public ConsumeEffects(float probability, String... effectsToBeRemoved) {
            this.type = "remove_effects";
            this.probability = probability;
            this.effectsToBeRemoved = effectsToBeRemoved;
        }

        public ConsumeEffects(float probability) {
            this.type = "clear_all_effects";
            this.probability = probability;
        }

        public ConsumeEffects(float probability, float diameter) {
            this.type = "teleport_randomly";
            this.probability = probability;
            this.diameter = diameter;
        }
    }
}
