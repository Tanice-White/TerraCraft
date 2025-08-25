package io.github.tanice.terraCraft.bukkit.util.adapter;

import io.github.tanice.terraCraft.bukkit.util.version.MCVersion;
import io.github.tanice.terraCraft.bukkit.util.version.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.util.version.ServerVersion;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public final class BukkitAttribute {
    private static final Map<String, BukkitAttribute> ATTRIBUTES = new HashMap<>();
    private static final Map<String, BukkitAttribute> ALIAS_MAP = new HashMap<>();
    private final String[] aliases;
    private final String key;
    private final MCVersion addedInVersion;

    public static final BukkitAttribute ATTACK_DAMAGE = BukkitAttribute.register("ATTACK_DAMAGE", new String[]{"DAMAGE", "GENERIC_ATTACK_DAMAGE", "ATTACKDAMAGE"}, "generic.attack_damage", "attack_damage");
    public static final BukkitAttribute ATTACK_SPEED = BukkitAttribute.register("ATTACK_SPEED", new String[]{"GENERIC_ATTACK_SPEED", "ATTACKSPEED", "WEAPON_SPEED", "WEAPONSPEED"}, "generic.attack_speed", "attack_speed");
    public static final BukkitAttribute MAX_HEALTH = BukkitAttribute.register("MAX_HEALTH", new String[]{"HEALTH", "GENERIC_MAX_HEALTH", "MAXHEALTH", "HP"}, "generic.max_health", "max_health");
    public static final BukkitAttribute MOVEMENT_SPEED = BukkitAttribute.register("MOVEMENT_SPEED", new String[]{"GENERIC_MOVEMENT_SPEED", "MOVEMENTSPEED", "MOVESPEED", "RUNSPEED", "RUN_SPEED"}, "generic.movement_speed", "movement_speed");
    public static final BukkitAttribute FLYING_SPEED = BukkitAttribute.register("FLYING_SPEED", new String[]{"GENERIC_FLYING_SPEED", "FLYINGSPEED", "FLY_SPEED", "FLYSPEED"}, "generic.flying_speed", "flying_speed");
    public static final BukkitAttribute ATTACK_KNOCKBACK = BukkitAttribute.register("ATTACK_KNOCKBACK", new String[]{"KNOCKBACK", "ATTACKKNOCKBACK", "GENERIC_ATTACK_KNOCKBACK"}, "generic.attack_knockback", "attack_knockback");
    public static final BukkitAttribute KNOCKBACK_RESISTANCE = BukkitAttribute.register("KNOCKBACK_RESISTANCE", new String[]{"GENERIC_KNOCKBACK_RESISTANCE", "KNOCKBACKRESISTANCE", "KNOCKBACKRESIST"}, "generic.knockback_resistance", "knockback_resistance");
    public static final BukkitAttribute ARMOR = BukkitAttribute.register("ARMOR", new String[]{"GENERIC_ARMOR"}, "generic.armor", "armor");
    public static final BukkitAttribute ARMOR_TOUGHNESS = BukkitAttribute.register("ARMOR_TOUGHNESS", new String[]{"GENERIC_ARMOR_TOUGHNESS", "TOUGHNESS", "ARMORTOUGHNESS"}, "generic.armor_toughness", "armor_toughness");
    public static final BukkitAttribute LUCK = BukkitAttribute.register("LUCK", new String[]{"GENERIC_LUCK"}, "generic.luck", "luck");
    public static final BukkitAttribute SCALE = BukkitAttribute.register("SCALE", new String[]{"GENERIC_SCALE"}, "generic.scale", "scale", MinecraftVersions.v1_20_5);
    public static final BukkitAttribute FOLLOW_RANGE = BukkitAttribute.register("FOLLOW_RANGE", new String[]{"GENERIC_FOLLOW_RANGE"}, "generic.follow_range", "follow_range");
    public static final BukkitAttribute FALL_DAMAGE_MULTIPLIER = BukkitAttribute.register("FALL_DAMAGE_MULTIPLIER", new String[]{"GENERIC_FALL_DAMAGE_MULTIPLIER", "FALLDAMAGEMULTIPLIER"}, "generic.fall_damage_multiplier", "fall_damage_multiplier", MinecraftVersions.v1_20_5);
    public static final BukkitAttribute SAFE_FALL_DISTANCE = BukkitAttribute.register("SAFE_FALL_DISTANCE", new String[]{"GENERIC_SAFE_FALL_DISTANCE", "SAFEFALLDISTANCE"}, "generic.safe_fall_distance", "safe_fall_distance", MinecraftVersions.v1_20_5);
    public static final BukkitAttribute GRAVITY = BukkitAttribute.register("GRAVITY", new String[]{"GENERIC_GRAVITY"}, "generic.gravity", "gravity", MinecraftVersions.v1_20_5);
    public static final BukkitAttribute BLOCK_BREAK_SPEED = BukkitAttribute.register("BLOCK_BREAK_SPEED", new String[]{"PLAYER_BLOCK_BREAK_SPEED", "BLOCKBREAKSPEED"}, "player.block_break_speed", "block_break_speed", MinecraftVersions.v1_20_5);
    public static final BukkitAttribute ENTITY_INTERACTION_RANGE = BukkitAttribute.register("ENTITY_INTERACTION_RANGE", new String[]{"PLAYER_ENTITY_INTERACTION_RANGE", "ENTITYINTERACTIONRANGE"}, "player.entity_interaction_range", "entity_interaction_range", MinecraftVersions.v1_20_5);
    public static final BukkitAttribute BLOCK_INTERACTION_RANGE = BukkitAttribute.register("BLOCK_INTERACTION_RANGE", new String[]{"PLAYER_BLOCK_INTERACTION_RANGE", "BLOCKINTERACTIONRANGE"}, "player.block_interaction_range", "block_interaction_range", MinecraftVersions.v1_20_5);
    public static final BukkitAttribute JUMP_HEIGHT = BukkitAttribute.register("JUMP_HEIGHT", new String[]{"GENERIC_JUMP_HEIGHT", "JUMPHEIGHT", "JUMPSTRENGTH", "GENERIC_JUMP_STRENGTH", "JUMP_STRENGTH"}, "generic.jump_strength", "jump_strength");
    public static final BukkitAttribute STEP_HEIGHT = BukkitAttribute.register("STEP_HEIGHT", new String[]{"GENERIC_STEP_HEIGHT", "STEPHEIGHT"}, "generic.step_height", "step_height", MinecraftVersions.v1_20_5);
    public static final BukkitAttribute MAX_ABSORPTION = BukkitAttribute.register("MAX_ABSORPTION", new String[]{"GENERIC_MAX_ABSORPTION", "MAXABSORPTION"}, "generic.max_absorption", "max_absorption", MinecraftVersions.v1_20_2);
    public static final BukkitAttribute BURNING_TIME = BukkitAttribute.register("BURNING_TIME", new String[]{"GENERIC_BURNING_TIME", "BURNINGTIME"}, "generic.burning_time", "burning_time", MinecraftVersions.v1_21);
    public static final BukkitAttribute EXPLOSION_KNOCKBACK_RESISTANCE = BukkitAttribute.register("EXPLOSION_KNOCKBACK_RESISTANCE", new String[]{"GENERIC_EXPLOSION_KNOCKBACK_RESISTANCE", "EXPLOSIONKNOCKBACKRESISTANCE"}, "generic.explosion_knockback_resistance", "explosion_knockback_resistance", MinecraftVersions.v1_21);
    public static final BukkitAttribute MOVEMENT_EFFICIENCY = BukkitAttribute.register("MOVEMENT_EFFICIENCY", new String[]{"GENERIC_MOVEMENT_EFFICIENCY", "MOVEMENTEFFICIENCY"}, "generic.movement_efficiency", "movement_efficiency", MinecraftVersions.v1_21);
    public static final BukkitAttribute OXYGEN = BukkitAttribute.register("OXYGEN", new String[]{"GENERIC_OXYGEN_BONUS", "OXYGEN_BONUS"}, "generic.oxygen_bonus", "oxygen_bonus", MinecraftVersions.v1_21);
    public static final BukkitAttribute SNEAKING_SPEED = BukkitAttribute.register("SNEAKING_SPEED", new String[]{"GENERIC_SNEAKING_SPEED", "SNEAKINGSPEED"}, "player.sneaking_speed", "sneaking_speed", MinecraftVersions.v1_21);
    public static final BukkitAttribute WATER_MOVEMENT_EFFICIENCY = BukkitAttribute.register("WATER_MOVEMENT_EFFICIENCY", new String[]{"GENERIC_WATER_MOVEMENT_EFFICIENCY", "WATERMOVEMENTEFFICIENCY"}, "generic.water_movement_efficiency", "water_movement_efficiency", MinecraftVersions.v1_21);
    public static final BukkitAttribute SUBMERGED_MINING_SPEED = BukkitAttribute.register("SUBMERGED_MINING_SPEED", new String[]{"PLAYER_SUBMERGED_MINING_SPEED", "SUBMERGEDMININGSPEED"}, "player.submerged_mining_speed", "submerged_mining_speed", MinecraftVersions.v1_21);
    public static final BukkitAttribute MINING_EFFICIENCY = BukkitAttribute.register("MINING_EFFICIENCY", new String[]{"PLAYER_MINING_EFFICIENCY", "PLAYERMININGEFFICIENCY", "MININGEFFICIENCY"}, "player.mining_efficiency", "mining_efficiency", MinecraftVersions.v1_21);
    public static final BukkitAttribute TEMPT_RANGE = BukkitAttribute.register("TEMPT_RANGE", new String[]{"GENERIC_TEMPT_RANGE", "TEMPTRANGE"}, "generic.tempt_range", "tempt_range", MinecraftVersions.v1_21_2);
    public static final BukkitAttribute SWEEPING_DAMAGE_RATIO = BukkitAttribute.register("SWEEPING_DAMAGE_RATIO", new String[]{"PLAYER_SWEEPING_DAMAGE_RATIO", "PLAYERSWEEPINGDAMAGERATIO"}, "player.sweeping_damage_ratio", "sweeping_damage_ratio", MinecraftVersions.v1_21);
    public static final BukkitAttribute SPAWN_REINFORCEMENTS = BukkitAttribute.register("SPAWN_REINFORCEMENTS", new String[]{"ZOMBIE_SPAWN_REINFORCEMENTS", "ZOMBIESPAWNREINFORCEMENTS", "SPAWNREINFORCMENTS"}, "zombie.spawn_reinforcements", "spawn_reinforcements");

    private BukkitAttribute(String[] aliases, String key, MCVersion addedInVersion) {
        this.aliases = aliases;
        this.key = key;
        this.addedInVersion = addedInVersion;
    }

    private static BukkitAttribute register(String name, String[] aliases, String keyPre21, String keyPost21) {
        return BukkitAttribute.register(name, aliases, keyPre21, keyPost21, MinecraftVersions.v1_18);
    }

    private static BukkitAttribute register(String name, String[] aliases, String keyPre21, String keyPost21, MCVersion addedInVersion) {
        String computedKey = ServerVersion.isBeforeOrEq(MinecraftVersions.v1_21_1) ? keyPre21 : keyPost21;
        BukkitAttribute attribute = new BukkitAttribute(aliases, computedKey, addedInVersion);
        ATTRIBUTES.put(name.toUpperCase(), attribute);
        ALIAS_MAP.put(name.toUpperCase(), attribute);
        for (String alias : aliases) {
            ALIAS_MAP.put(alias.toUpperCase(), attribute);
        }
        return attribute;
    }

    public static Collection<BukkitAttribute> values() {
        return ATTRIBUTES.values().stream().filter(attribute -> ServerVersion.isBeforeOrEq(attribute.addedInVersion)).collect(Collectors.toSet());
    }

    public static BukkitAttribute get(String name) {
        return ALIAS_MAP.get(name.toUpperCase());
    }

    public static String getAttributeName(String name) {
        BukkitAttribute attribute = BukkitAttribute.get(name);
        return attribute != null ? attribute.key : name;
    }

    public static String getAttributeKey(String name) {
        BukkitAttribute attribute = BukkitAttribute.get(name);
        return attribute != null ? "minecraft:" + attribute.key : name;
    }

    @Nullable
    public static Attribute getAttribute(String name) {
        BukkitAttribute attribute = BukkitAttribute.get(name);
        if (attribute != null) return attribute.getBukkitAttribute();

        try {
            if (!name.contains(":")) name = "minecraft:" + name;
            return Registry.ATTRIBUTE.get(NamespacedKey.fromString(name));

        } catch (Exception exception) {
            return null;
        }
    }

    public Attribute getBukkitAttribute() {
        return Registry.ATTRIBUTE.get(this.getAttributeKey());
    }

    public NamespacedKey getAttributeKey() {
        return NamespacedKey.minecraft(this.key);
    }
}
