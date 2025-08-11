package io.github.tanice.terraCraft.bukkit.utils.adapter;

import io.github.tanice.terraCraft.core.utils.namespace.TerraNamespaceKey;

public class BukkitEffect {
    private final TerraNamespaceKey id;
    private final boolean ambient;
    private final int amplifier;
    private final int duration;
    private final BukkitEffect hiddenEffect;
    private final boolean showIcon;
    private final boolean showParticles;

    public BukkitEffect(String key, boolean ambient, int amplifier, int duration, BukkitEffect hiddenEffect, boolean showIcon, boolean showParticles) {
        this.id = TerraNamespaceKey.minecraft(key);
        this.ambient = ambient;
        /* 确保amplifier在0-255范围内 */
        this.amplifier = (amplifier & 0xFF);
        this.duration = duration;
        this.hiddenEffect = hiddenEffect;
        this.showIcon = showIcon;
        this.showParticles = showParticles;
    }

    public boolean isAmbient() {
        return ambient;
    }

    public int getAmplifier() {
        return amplifier;
    }

    public byte getAmplifierAsByte() {
        return (byte) amplifier;
    }

    public int getDuration() {
        return duration;
    }

    public BukkitEffect getHiddenEffect() {
        return hiddenEffect;
    }

    public String getId() {
        return id.get();
    }

    public boolean isShowIcon() {
        return showIcon;
    }

    public boolean isShowParticles() {
        return showParticles;
    }
}
