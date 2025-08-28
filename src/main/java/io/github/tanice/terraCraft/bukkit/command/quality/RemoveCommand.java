package io.github.tanice.terraCraft.bukkit.command.quality;

import io.github.tanice.terraCraft.api.item.component.TerraQualityComponent;
import io.github.tanice.terraCraft.bukkit.command.CommandRunner;
import io.github.tanice.terraCraft.bukkit.item.component.QualityComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static io.github.tanice.terraCraft.api.command.TerraCommand.*;

public class RemoveCommand extends CommandRunner {

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String getDescription() {
        return "remove the quality of the item";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length != 0) {
            sender.sendMessage(RED + "Invalid number of arguments");
            return true;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage(RED + "This command can only be executed by players");
            return true;
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.isEmpty()) {
            sender.sendMessage(RED + "Please hold terra item in mainhand");
            return true;
        }
        TerraQualityComponent qualityComponent = QualityComponent.from(item);
        if (qualityComponent == null) {
            sender.sendMessage(GOLD + "Item does not have quality component");
            return true;
        }
        qualityComponent.setQuality(null);
        qualityComponent.cover(item);
        sender.sendMessage(GREEN + "Remove item quality successfully");
        return true;
    }
}
