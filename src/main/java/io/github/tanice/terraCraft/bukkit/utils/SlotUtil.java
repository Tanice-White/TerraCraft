package io.github.tanice.terraCraft.bukkit.utils;

import java.util.*;

public final class SlotUtil {

    public static boolean isMainHand(String slot) {
        return Slot.HAND.matches(slot);
    }

    public static boolean isOffHand(String slot) {
        return Slot.OFF_HAND.matches(slot);
    }

    public static boolean isHelmet(String slot) {
        return Slot.HEAD.matches(slot);
    }

    public static boolean isChestplate(String slot) {
        return Slot.CHEST.matches(slot);
    }

    public static boolean isLeggings(String slot) {
        return Slot.LEGS.matches(slot);
    }

    public static boolean isBoots(String slot) {
        return Slot.FEET.matches(slot);
    }

    public static boolean isAccessory(String slot) {
        return Slot.ACCESSORY.matches(slot);
    }

    public enum Slot {
        HAND("mainhand", "hand"),
        OFF_HAND("offhand", "hand"),
        HEAD("head", "helmet", "equipment"),
        CHEST("chest", "chestplate", "equipment"),
        LEGS("legs", "leggings", "equipment"),
        FEET("feet", "boots", "equipment"),
        ACCESSORY("accessory"),;

        private final Set<String> aliases;
        Slot(String... aliases) {
            this.aliases = new HashSet<>();
            for (String alias : aliases) this.aliases.add(alias.toLowerCase());
            this.aliases.add("any");
        }

        public boolean matches(String slot) {
            return slot != null && aliases.contains(slot.toLowerCase());
        }
    }
}
