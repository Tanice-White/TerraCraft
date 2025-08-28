package io.github.tanice.terraCraft.bukkit.command.level;

import io.github.tanice.terraCraft.api.item.component.TerraInnerNameComponent;
import io.github.tanice.terraCraft.api.item.component.TerraLevelComponent;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.command.CommandRunner;
import io.github.tanice.terraCraft.bukkit.item.component.LevelComponent;
import io.github.tanice.terraCraft.bukkit.item.component.TerraNameComponent;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

import static io.github.tanice.terraCraft.api.command.TerraCommand.*;

public class LevelUpCommand extends CommandRunner {

    @Override
    public String getName() {
        return "up";
    }

    @Override
    public String getUsage() {
        return """
                up
                up <ignore_chance>
                """;
    }

    @Override
    public String getDescription() {
        return "Level up the item in mainhand, using zhe material in offhand, and able to ignore the chance";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(RED + "This command can only be executed by players");
            return true;
        }
        if (args.length > 1) {
            sender.sendMessage(RED + "Too many arguments");
            return true;
        }

        boolean ignoreChance = false;
        if (args.length == 1) ignoreChance = Boolean.parseBoolean(args[0]);

        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        TerraLevelComponent levelComponent = LevelComponent.from(mainHandItem);
        if (levelComponent == null) {
            sender.sendMessage(GOLD + "Item does not have level component");
            return true;
        }
        ItemStack offHandItem = player.getInventory().getItemInOffHand();
        if (offHandItem.isEmpty()) {
            sender.sendMessage(RED + "Please hold required material in offhand");
            return true;
        }

        boolean ig = ignoreChance;
        TerraCraftBukkit.inst().getItemManager().getLevelTemplate(levelComponent.getTemplate()).ifPresentOrElse(terraLevelTemplate -> {
            int level = levelComponent.getLevel();
            /* 验证副手物品 */
            TerraInnerNameComponent offhandItemNameComponent = TerraNameComponent.from(offHandItem);
            if (offhandItemNameComponent == null) {
                sender.sendMessage(GOLD + "Invalid material for mainhand item level up");
                return;
            }
            if (!offhandItemNameComponent.getName().equals(terraLevelTemplate.getMaterial())) {
                sender.sendMessage(RED + "Offhand item does not match required material");
                return;
            }
            /* 等级范围验证 */
            if (terraLevelTemplate.getMax() - terraLevelTemplate.getBegin() <= level) {
                sender.sendMessage(GOLD + "Level has reached the maximum value");
                return;
            }
            /* 成功 */
            if (ig || Math.random() < terraLevelTemplate.getChance()) {
                levelComponent.setLevel(level + 1);
                levelComponent.cover(mainHandItem);
                sender.sendMessage(GREEN + "Level up successfully");
            /* 失败 */
            } else {
                String res = RED + "Level up failed";
                if (terraLevelTemplate.isFailedLevelDown() && level > 0) {
                    levelComponent.setLevel(level - 1);
                    levelComponent.cover(mainHandItem);
                    res += ", level down";
                }
                player.sendMessage(res);
            }
            offHandItem.setAmount(offHandItem.getAmount() - 1);
        }, () -> {
            TerraInnerNameComponent nameComponent = TerraNameComponent.from(mainHandItem);
            sender.sendMessage(RED + "Level template does not exist");
            TerraCraftLogger.error("Item: " + (nameComponent == null ? mainHandItem.displayName() : nameComponent.getName()) +
                    " in player: " + player.getName() + " has Invalid template name");
        });
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) return List.of("true", "false");
        return Collections.emptyList();
    }
}
