package io.github.tanice.terraCraft.bukkit.command.item;

import io.github.tanice.terraCraft.api.item.component.*;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.command.CommandRunner;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

import static io.github.tanice.terraCraft.api.command.TerraCommand.*;
import static io.github.tanice.terraCraft.api.command.TerraCommand.RESET;
import static io.github.tanice.terraCraft.bukkit.util.TerraComponentUtil.getTerraComponentFrom;

public class ItemCheckCommand extends CommandRunner {
    @Override
    public String getName() {
        return "check";
    }

    @Override
    public String getDescription() {
        return "check terraMetas of the item in mainhand or a terra item in config file";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(RED + "This command can only be executed by players");
            return true;
        }
        if (args.length == 0) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.isEmpty()) {
                sender.sendMessage(YELLOW + "You must hold an item in your main hand");
                return true;
            }

            StringBuilder sb = new StringBuilder();
            sb.append(BOLD).append(GREEN).append("Terra Components in the item").append(RESET).append("\n");
            for (TerraBaseComponent component : getTerraComponentFrom(item)) sb.append(component).append("\n");
            sender.sendMessage(sb.toString());
            return true;
        }
        if (args.length == 1) {
            TerraCraftBukkit.inst().getItemManager().getItem(args[0])
                    .ifPresentOrElse(baseItem -> sender.sendMessage(baseItem.toString()), () -> sender.sendMessage(RED + "No terra named " + args[0]));
            return true;
        }
        sender.sendMessage(RED + "Too many arguments");
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) return TerraCraftBukkit.inst().getItemManager().filterItems(args[0]).stream().sorted().toList();;
        return Collections.emptyList();
    }
}
