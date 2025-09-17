package io.github.tanice.terraCraft.bukkit.command.attribute;

import io.github.tanice.terraCraft.api.attribute.calculator.TerraAttributeCalculator;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.command.CommandRunner;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static io.github.tanice.terraCraft.api.command.TerraCommand.*;

public class GetCommand extends CommandRunner {
    @Override
    public String getName() {
        return "get";
    }

    @Override
    public String getDescription() {
        return "get final attribute for the player";
    }

    @Override
    public String getUsage() {
        return """
                get
                get <player>
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
        TerraAttributeCalculator calculator = TerraCraftBukkit.inst().getEntityAttributeManager().getAttributeCalculator(player);
        sender.sendMessage(GREEN + "Player " + YELLOW + player.getName() + GREEN + " attribute:\n" + calculator.getMeta());
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return playerList(args[0]);
    }
}
