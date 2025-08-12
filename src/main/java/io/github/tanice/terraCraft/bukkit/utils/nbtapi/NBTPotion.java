package io.github.tanice.terraCraft.bukkit.utils.nbtapi;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.github.tanice.terraCraft.core.utils.namespace.TerraNamespaceKey;

import javax.annotation.Nonnull;

public class NBTPotion {
    @Nonnull
    private final TerraNamespaceKey id;
    private final Boolean ambient;
    private final Integer amplifier;
    private final Integer duration;
    private final NBTPotion hiddenEffect;
    private final Boolean showIcon;
    private final Boolean showParticles;

    /**
     * @param hiddenEffect 同类型等级更低的药水效果
     */
    public NBTPotion(@Nonnull String key, Boolean ambient, Integer amplifier, Integer duration, NBTPotion hiddenEffect, Boolean showIcon, Boolean showParticles) {
        this.id = TerraNamespaceKey.minecraft(key);
        this.ambient = ambient;
        this.amplifier = amplifier;
        this.duration = duration;
        this.hiddenEffect = hiddenEffect;
        this.showIcon = showIcon;
        this.showParticles = showParticles;
    }

    public Boolean isAmbient() {
        return ambient;
    }

    public Integer getAmplifier() {
        return amplifier;
    }

    public Byte getAmplifierAsByte() {
        if (amplifier == null) return null;
        return amplifier.byteValue();
    }

    public Integer getDuration() {
        return duration;
    }

    public NBTPotion getHiddenEffect() {
        return hiddenEffect;
    }

    public String getId() {
        return id.get();
    }

    public Boolean isShowIcon() {
        return showIcon;
    }

    public Boolean isShowParticles() {
        return showParticles;
    }

    public void addToCompound(ReadWriteNBT compound) {
        compound.setString("id", id.get());
        if (ambient != null) compound.setBoolean("ambient", ambient);
        if (amplifier != null) compound.setByte("amplifier", amplifier.byteValue());
        if (duration != null) compound.setInteger("duration", duration);
        if (showIcon != null) compound.setBoolean("show_icon", showIcon);
        if (showParticles != null) compound.setBoolean("show_particles", showParticles);

        if (hiddenEffect != null) {
            ReadWriteNBT hiddenCompound = compound.getOrCreateCompound("hidden_effect");
            addHiddenToCompound(hiddenCompound, hiddenEffect);
        }
    }

    private void addHiddenToCompound(ReadWriteNBT compound, NBTPotion hiddenPotion) {
        if (hiddenPotion.ambient != null) compound.setBoolean("ambient", hiddenPotion.ambient);
        if (hiddenPotion.amplifier != null) compound.setByte("amplifier", hiddenPotion.amplifier.byteValue());
        if (hiddenPotion.duration != null) compound.setInteger("duration", hiddenPotion.duration);
        if (hiddenPotion.showIcon != null) compound.setBoolean("show_icon", hiddenPotion.showIcon);
        if (hiddenPotion.showParticles != null) compound.setBoolean("show_particles", hiddenPotion.showParticles);

        if (hiddenPotion.hiddenEffect != null) {
            ReadWriteNBT hiddenNBT = compound.getOrCreateCompound("hidden_effect");
            addHiddenToCompound(hiddenNBT, hiddenPotion.hiddenEffect);
        }
    }
}
