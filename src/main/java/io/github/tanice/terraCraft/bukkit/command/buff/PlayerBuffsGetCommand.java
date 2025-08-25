package io.github.tanice.terraCraft.bukkit.command.buff;

import io.github.tanice.terraCraft.api.buff.TerraBuffRecord;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.command.CommandRunner;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static io.github.tanice.terraCraft.api.command.TerraCommand.*;

public class PlayerBuffsGetCommand extends CommandRunner {
    @Override
    public String getName() {
        return "get";
    }

    @Override
    public String getDescription() {
        return "get TerraCraft buffs record of a player";
    }

    @Override
    public String getUsage() {
        return "get <player>";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(RED + "Missing player name");
            return true;
        }
        List<String> matches = playerList(args[0]);
        if (matches.isEmpty()) {
            sender.sendMessage(RED + "Player: " + args[1] + " does not exist");
            return true;
        }
        if (matches.size() > 1) {
            sender.sendMessage(RED + "Too many players: " + matches.size());
            return true;
        }

        Player player = Bukkit.getPlayer(matches.get(0));
        sender.sendMessage(GOLD + "buffs carried in player " + player.getName());
        for (TerraBuffRecord record : TerraCraftBukkit.inst().getBuffManager().getEntityActiveBuffRecords(player)) {
            sender.sendMessage(AQUA + record.toString());
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return playerList(args[0]);
    }
}
