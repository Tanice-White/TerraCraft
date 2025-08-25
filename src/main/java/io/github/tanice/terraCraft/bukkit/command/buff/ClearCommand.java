package io.github.tanice.terraCraft.bukkit.command.buff;

import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.command.CommandRunner;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static io.github.tanice.terraCraft.api.command.TerraCommand.*;

public class ClearCommand extends CommandRunner {
    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getDescription() {
        return "clear all buffs for the player";
    }

    @Override
    public String getUsage() {
        return "clear <player>";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        List<String> matches = playerList(args[0]);
        if (matches.isEmpty()) {
            sender.sendMessage(RED + "Player: " + args[0] + " does not exist");
            return true;
        }
        if (matches.size() > 1) {
            sender.sendMessage(RED + "Too many players: " + matches.size());
            return true;
        }

        Player player = Bukkit.getPlayer(matches.get(0));
        int num = TerraCraftBukkit.inst().getBuffManager().getEntityActiveBuffRecords(player).size();
        TerraCraftBukkit.inst().getBuffManager().unregister(player);
        sender.sendMessage(GREEN + "Successfully cleared all buffs(" + num + ") for player: " + player.getName());
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return playerList(args[0]);
    }
}
