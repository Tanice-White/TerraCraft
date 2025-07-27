package io.github.tanice.terraCraft.bukkit.items;

import io.github.tanice.terraCraft.api.buffs.TerraBaseBuff;
import io.github.tanice.terraCraft.api.items.TerraEdible;
import io.github.tanice.terraCraft.api.players.TerraPlayerData;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.events.entity.TerraAttributeUpdateEvent;
import io.github.tanice.terraCraft.bukkit.events.entity.TerraPlayerDataLimitChangeEvent;
import io.github.tanice.terraCraft.bukkit.events.entity.TerraPlayerEatEvent;
import io.github.tanice.terraCraft.bukkit.utils.annotation.NonnullByDefault;
import io.github.tanice.terraCraft.bukkit.utils.events.TerraEvents;
import io.github.tanice.terraCraft.core.items.AbstractItem;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import io.github.tanice.terraCraft.core.players.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nullable;
import java.util.*;

import static io.github.tanice.terraCraft.core.constants.ConfigKeys.*;

/**
 * 可食用物品
 */
@NonnullByDefault
public class Edible extends AbstractItem implements TerraEdible {
    /** 食用cd(s) */
    private final int cd;
    /** 食用次数 */
    private final int times;

    private final TerraPlayerData changedPlayerData;
    private final int food;
    private final int level;
    private final float saturation;

    private final List<TerraBaseBuff> buffs;
    private final List<String> commandLore;
    private final List<PotionEffect> effects;
    private final boolean isLimitChange;

    private final EatSound sound;

    public Edible(String name, ConfigurationSection cfg) {
        super(name, cfg);

        this.cd = cfg.getInt(CD, -1);
        this.times = cfg.getInt(TIMES, -1);

        this.changedPlayerData = PlayerData.from(cfg);
        this.food = cfg.getInt(FOOD, 0);
        this.level = cfg.getInt(LEVEL, 0);
        this.saturation = (float) cfg.getDouble(SATURATION, 0D);

        this.buffs = new ArrayList<>();
        this.commandLore = cfg.getStringList(COMMAND);
        this.effects = new ArrayList<>();

        this.isLimitChange = changedPlayerData.getMaxMana() != 0 || changedPlayerData.getMaxHealth() != 0;

        this.sound = new EatSound(cfg.getString(SOUND));

        this.initBuffs(cfg.getStringList(BUFF));
        this.initEffects(cfg.getStringList(EFFECT));
    }

    @Override
    public int getCd() {
        return this.cd;
    }

    @Override
    public int getTimes() {
        return this.times;
    }

    @Override
    public boolean apply(Player player) {
        PlayerData playerData = PlayerData.from(player);

        TerraPlayerEatEvent event1 = TerraEvents.callAndReturn(new TerraPlayerEatEvent(player, this));
        if (event1.isCancelled()) return false;

        if (isLimitChange) {
            TerraPlayerDataLimitChangeEvent event2 = TerraEvents.callAndReturn(new TerraPlayerDataLimitChangeEvent(player, changedPlayerData));
            if (event2.isCancelled()) return false;
        }

        playSound(player);
        playerData.merge(changedPlayerData);

        /* 基础生效 */
        player.setFoodLevel(player.getFoodLevel() + food);
        player.setSaturation(player.getSaturation() + saturation);
        player.setLevel(player.getLevel() + level);

        if (!buffs.isEmpty()) {
            if (TerraCraftBukkit.inst().getBuffManager().activateBuffs(player, buffs))
                TerraEvents.call(new TerraAttributeUpdateEvent(player));
        }
        if (!effects.isEmpty()) player.addPotionEffects(effects);
        if (!commandLore.isEmpty()) {
            for (String cn : commandLore) {
                if (cn.isEmpty()) continue;
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cn.replace("@self", player.getName()));
            }
        }
        player.setCooldown(this.item, this.cd);
        return true;
    }

    /**
     * 发出吃东西的声音
     */
    private void playSound(Player player) {
        if (!this.sound.enable || this.sound.soundName == null) return;
        player.getWorld().playSound(player.getLocation(), this.sound.soundName, this.sound.soundVolume, this.sound.soundPitch);
    }

    private void initBuffs(Collection<String> buffLore) {
        String[] v;
        Optional<TerraBaseBuff> buffOption;
        TerraBaseBuff buff;
        for (String bn : buffLore) {
            v = bn.trim().split("\\s+");
            if (v.length > 3) {
                TerraCraftLogger.error("Edible: " + name + " - Custom buff: " + bn + " format error");
                continue;
            }

            buffOption = TerraCraftBukkit.inst().getBuffManager().getBuff(v[0]);
            if (buffOption.isEmpty()) {
                TerraCraftLogger.error("Edible: " + name + " - Custom buff: " + bn + " error, corresponding buff definition not found");
                continue;
            }
            buff = buffOption.get();

            int duration;
            double chance;
            for (int i = 1; i < v.length; i++) {
                String param = v[i];
                if (param.startsWith("#")) {
                    duration = Integer.parseInt(param.substring(1));
                    if (duration > 0) buff.setDuration(duration);

                } else if (param.startsWith("%")) {
                    chance = Double.parseDouble(param.substring(1));
                    if (chance > 0 && chance <= 1) buff.setChance(chance);

                }
            }
            buffs.add(buff);
        }
    }

    private void initEffects(Collection<String> effectLore) {
        if (effectLore.isEmpty()) return;
        String[] v;

        for (String en : effectLore) {
            v = en.split(" ");
            if (v.length != 3) {
                TerraCraftLogger.error("Edible: " + name + " - Original potion effect: " + en + " format error");
                continue;
            }
            PotionEffectType effectType = Registry.EFFECT.get(NamespacedKey.minecraft(v[0].toLowerCase()));
            if (effectType == null) {
                TerraCraftLogger.error("Original effect name unrecognized: " + v[0] + " in edible: " + name);
                continue;
            }
            this.effects.add(
                    new PotionEffect(
                    effectType,
                    Math.max(Integer.parseInt(v[2]), 0),
                    Math.max(Integer.parseInt(v[1]) - 1, 0),
                    true,            // 是否显示粒子效果
                    true,            // 是否显示状态图标
                    false             // 是否有环境音效
                    )
            );
        }
    }

    private static class EatSound {
        boolean enable;
        @Nullable
        Sound soundName;
        float soundVolume;
        float soundPitch;

        EatSound(@Nullable String lore) {
            if (lore == null || lore.isEmpty()) {
                enable = false;
                return;
            }
            String[] sound = lore.split(" ");
            if (sound.length != 3) {
                TerraCraftLogger.error("Invalid sound configuration in edible items: " + lore);
                enable = false;
                return;
            }
            String[] k = sound[0].split(":");
            if (k.length == 2) soundName = Registry.SOUNDS.get(new NamespacedKey(k[0], k[1]));
            else soundName = Registry.SOUNDS.get(NamespacedKey.minecraft(sound[0]));
            soundVolume = Float.parseFloat(sound[1]);
            soundPitch = Float.parseFloat(sound[2]);
            enable = true;
        }
    }
}
