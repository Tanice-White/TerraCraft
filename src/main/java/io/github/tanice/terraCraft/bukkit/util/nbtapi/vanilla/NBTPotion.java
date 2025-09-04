package io.github.tanice.terraCraft.bukkit.util.nbtapi.vanilla;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.github.tanice.terraCraft.core.util.logger.TerraCraftLogger;
import io.github.tanice.terraCraft.core.util.namespace.TerraNamespaceKey;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nullable;
import java.util.Objects;

public class NBTPotion {
    private final TerraNamespaceKey id;
    @Nullable
    private final Boolean ambient;
    @Nullable
    private final Integer amplifier;
    @Nullable
    private final Integer duration;
    @Nullable
    private final NBTPotion hiddenEffect;
    @Nullable
    private final Boolean showIcon;
    @Nullable
    private final Boolean showParticles;

    /**
     * @param hiddenEffect 同类型等级更低的药水效果
     */
    public NBTPotion(TerraNamespaceKey id, @Nullable Boolean ambient, @Nullable Integer amplifier, @Nullable Integer duration, @Nullable NBTPotion hiddenEffect, @Nullable Boolean showIcon, @Nullable Boolean showParticles) {
        this.id = id;
        this.ambient = ambient;
        this.amplifier = amplifier;
        this.duration = duration;
        this.hiddenEffect = hiddenEffect;
        this.showIcon = showIcon;
        this.showParticles = showParticles;
    }

    @Nullable
    public static NBTPotion from(String id, @Nullable ConfigurationSection cfg) {
        if (cfg == null) return null;
        TerraNamespaceKey nk = TerraNamespaceKey.from(id);
        if (nk == null) {
            TerraCraftLogger.warning("Invalid NBTPotion ID: " + cfg.getString("id"));
            return null;
        }
        return new NBTPotion(
                nk,
                cfg.isSet("ambient") ? cfg.getBoolean("ambient") : null,
                cfg.isSet("amplifier") ? cfg.getInt("amplifier") : null,
                cfg.isSet("duration") ? cfg.getInt("duration") : null,
                NBTPotion.from("inner", cfg.getConfigurationSection("hidden")),
                cfg.isSet("icon") ? cfg.getBoolean("icon") : null,
                cfg.isSet("particles") ? cfg.getBoolean("particles") : null
        );
    }

    @Nullable
    public Boolean isAmbient() {
        return ambient;
    }

    @Nullable
    public Integer getAmplifier() {
        return amplifier;
    }

    @Nullable
    public Byte getAmplifierAsByte() {
        if (amplifier == null) return null;
        return amplifier.byteValue();
    }

    @Nullable
    public Integer getDuration() {
        return duration;
    }

    @Nullable
    public NBTPotion getHiddenEffect() {
        return hiddenEffect;
    }
    public String getId() {
        return id.get();
    }

    @Nullable
    public Boolean isShowIcon() {
        return showIcon;
    }

    @Nullable
    public Boolean isShowParticles() {
        return showParticles;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ambient, amplifier, duration, showIcon, showParticles, hiddenEffect);
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
