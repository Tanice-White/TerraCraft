package io.github.tanice.terraCraft.core.utils.helper.mythicmobs;

import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.entity.Player;

public final class MMHelper {
    public static boolean castSkill(Player player, String skillName) {
        return MythicBukkit.inst().getAPIHelper().castSkill(player, skillName);
    }
}
