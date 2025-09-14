package io.github.tanice.terraCraft.bukkit.command.attribute;

import io.github.tanice.terraCraft.api.attribute.calculator.TerraAttributeCalculator;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.command.CommandRunner;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static io.github.tanice.terraCraft.api.command.TerraCommand.*;
import static io.github.tanice.terraCraft.api.command.TerraCommand.GREEN;

public class PlayerAttributeSetCommand extends CommandRunner {
    @Override
    public String getName() {
        return "set";
    }

    @Override
    public String getDescription() {
        return "set final attribute for the player";
    }

    @Override
    public String getUsage() {
        return """
                set <attribute> <value>
                set <attribute> <value> <player>
                """;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 2 || args.length > 3) {
            sender.sendMessage(RED + "Invalid number of arguments");
            return true;
        }

        // TODO 每个玩家储存一个基础的meta



        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(RED + "Invalid player name: " + args[0]);
            return true;
        }
        TerraAttributeCalculator calculator = TerraCraftBukkit.inst().getEntityAttributeManager().getAttributeCalculator(player);
        sender.sendMessage(GREEN + "Player " + YELLOW + player.getName() + GREEN + " attribute:\n" + calculator.getMeta());
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 0) return List.of("external_health", "max_mana", "mana");
        return playerList(args[0]);
    }
}
