package io.github.tanice.terraCraft.bukkit.utils.nbtapi;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class NBTEffect {
    @Nonnull
    private final String type;
    private final float probability;
    private NBTPotion[] effects;
    private NBTSound sound;
    private String[] effectsToBeRemoved;
    private float diameter;

    public NBTEffect(float probability, @Nonnull NBTPotion[] effects) {
        this.type = "apply_effects";
        this.probability = probability;
        this.effects = effects;
    }

    public NBTEffect(float probability, @Nonnull NBTSound sound) {
        this.type = "play_sound";
        this.probability = probability;
        this.sound = sound;
    }

    public NBTEffect(float probability, @Nonnull String[] effectsToBeRemoved) {
        this.type = "remove_effects";
        this.probability = probability;
        this.effectsToBeRemoved = effectsToBeRemoved;
    }

    public NBTEffect(float probability) {
        this.type = "clear_all_effects";
        this.probability = probability;
    }

    public NBTEffect(float probability, float diameter) {
        this.type = "teleport_randomly";
        this.probability = probability;
        this.diameter = diameter;
    }

    @Nonnull
    public String getType() {
        return this.type;
    }

    public float getProbability() {
        return this.probability;
    }

    public NBTPotion[] getEffects() {
        return this.effects;
    }

    public NBTSound getSound() {
        return this.sound;
    }

    public String[] getEffectsToBeRemoved() {
        return this.effectsToBeRemoved;
    }

    public float getDiameter() {
        return this.diameter;
    }

    public void addToCompound(ReadWriteNBT compound) {
        ReadWriteNBT sComponent;
        compound.setString("type", type);
        compound.setFloat("probability", probability);
        switch (type) {
            case "apply_effects" -> {
                for (NBTPotion potion : effects) {
                    sComponent = compound.getCompoundList("effects").addCompound();
                    potion.addToCompound(sComponent);
                }
            }
            case "clear_all_effects" -> compound.getCompoundList("effects").clear();
            case "play_sound" -> {
                sComponent = compound.getOrCreateCompound("sound");
                sComponent.setFloat("range", sound.getRange());
                sComponent.setString("sound_id", sound.getId());
            }
            case "remove_effects" -> compound.getStringList("effects").addAll(Arrays.stream(effectsToBeRemoved).toList());
            case "teleport_randomly" -> compound.setFloat("diameter", diameter);
        }
    }
}
