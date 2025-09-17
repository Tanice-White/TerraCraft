package io.github.tanice.terraCraft.bukkit.command.attribute;

import io.github.tanice.terraCraft.bukkit.command.CommandRunner;
import io.github.tanice.terraCraft.bukkit.util.nbtapi.NBTPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static io.github.tanice.terraCraft.api.command.TerraCommand.*;
import static io.github.tanice.terraCraft.api.command.TerraCommand.GREEN;

public class ClearCommand extends CommandRunner {
    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getDescription() {
        return "clear all terra meta for the player";
    }

    @Override
    public String getUsage() {
        return """
            clear
            clear <player>
            """;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length > 1) {
            sender.sendMessage(RED + "Invalid number of arguments");
            return true;
        }
        Player player = args.length == 1 ? Bukkit.getPlayer(args[0]) : (sender instanceof Player ? (Player) sender : null);
        if (player == null) {
            sender.sendMessage(RED + "Invalid target player");
            return true;
        }
        NBTPlayer.remove(player);
        sender.sendMessage(GREEN + "Player " + YELLOW + player.getName() + GREEN + " cleared");
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return playerList(args[0]);
    }
}
