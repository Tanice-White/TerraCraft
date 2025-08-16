package io.github.tanice.terraCraft.bukkit.utils.nbtapi;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBTCompoundList;
import de.tr7zw.nbtapi.iface.ReadWriteNBTList;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NBTEffect {
    private final String type;
    private final Float probability;
    private final NBTPotion[] effects;
    private final NBTSound sound;
    private final String[] effectsToBeRemoved;
    private final Float diameter;

    public NBTEffect(String type, @Nullable Float probability, @Nullable NBTPotion[] effects, @Nullable NBTSound sound, @Nullable String[] effectsToBeRemoved, @Nullable Float diameter) {
        this.type = type;
        this.probability = probability;
        this.effects = effects;
        this.sound = sound;
        this.effectsToBeRemoved = effectsToBeRemoved;
        this.diameter = diameter;
    }

    @Nullable
    public static NBTEffect from(String type,@Nullable ConfigurationSection cfg) {
        if (cfg == null) return null;
        Float chance = cfg.isSet("chance") ? (float) cfg.getDouble("chance") : null;
        switch (type) {
            case "apply_effects":
                List<NBTPotion> potions = new ArrayList<>();
                ConfigurationSection subCfg = cfg.getConfigurationSection("potions");
                if (subCfg != null) {
                    for (String key : subCfg.getKeys(false)) {
                        potions.add(NBTPotion.from(key, subCfg.getConfigurationSection(key)));
                    }
                }
                return new NBTEffect(type, chance, potions.toArray(new NBTPotion[0]), null, null, null);

            case "play_sound":
                NBTSound sound = NBTSound.form(cfg.getConfigurationSection("sound"));
                if (sound == null) {
                    TerraCraftLogger.warning("Invalid sound: " + cfg.getString("sound") + " in " + cfg.getCurrentPath());
                    return null;
                }
                return new NBTEffect(type, chance, null, sound, null, null);

            case "remove_effects":
                return new NBTEffect(type, chance, null, null, cfg.getStringList("remove").toArray(new String[0]), null);

            case "clear_all_effects":
                return new NBTEffect(type, chance, null, null, null, null);

            case "teleport_randomly":
                return new NBTEffect(type, chance, null, null, null, cfg.isSet("diameter") ? (float) cfg.getDouble("diameter") : null);

            default:
                TerraCraftLogger.warning("Invalid effect type: " + type + " in " + cfg.getCurrentPath());
                return null;
        }
    }


    public void addToCompound(ReadWriteNBT compound) {
        ReadWriteNBT sComponent;
        compound.setString("type", type);
        if (probability != null) compound.setFloat("probability", probability);
        switch (type) {
            case "apply_effects" -> {
                ReadWriteNBTCompoundList list = compound.getCompoundList("effects");
                if (effects == null) return;
                for (NBTPotion potion : effects) {
                    sComponent = list.addCompound();
                    potion.addToCompound(sComponent);
                }
            }
            case "clear_all_effects" -> compound.getCompoundList("effects").clear();
            case "play_sound" -> {
                if (sound != null) sound.addToCompound(compound.getOrCreateCompound("sound"));
            }
            case "remove_effects" -> {
                ReadWriteNBTList<String> list = compound.getStringList("effects");
                if (effectsToBeRemoved == null) return;
                list.addAll(Arrays.stream(effectsToBeRemoved).toList());
            }
            case "teleport_randomly" -> {
                if(diameter != null) compound.setFloat("diameter", diameter);
            }
            default -> TerraCraftLogger.warning("Invalid effect type: " + type);
        }
    }
}
