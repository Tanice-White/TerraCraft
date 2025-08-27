package io.github.tanice.terraCraft.bukkit.command.level;

import io.github.tanice.terraCraft.api.item.component.TerraLevelComponent;
import io.github.tanice.terraCraft.bukkit.command.CommandRunner;
import io.github.tanice.terraCraft.bukkit.item.component.LevelComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static io.github.tanice.terraCraft.api.command.TerraCommand.*;

public class LevelDownCommand extends CommandRunner {

    @Override
    public String getName() {
        return "down";
    }

    @Override
    public String getDescription() {
        return "Level down the item in mainhand";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(RED + "This command can only be executed by players");
            return true;
        }
        if (args.length > 0) {
            sender.sendMessage(RED + "Too many arguments");
            return true;
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        TerraLevelComponent levelComponent = LevelComponent.from(item);
        if (levelComponent == null) {
            sender.sendMessage(GOLD + "Item does not have level component");
            return true;
        }
        int level = levelComponent.getLevel();
        if (level == 0) {
            sender.sendMessage(GOLD + "Level has reached the minimum value");
            return true;
        }
        levelComponent.setLevel(level - 1);
        LevelComponent.clear(item);
        levelComponent.apply(item);
        sender.sendMessage(GREEN + "Level down Successfully");
        return true;
    }
}
