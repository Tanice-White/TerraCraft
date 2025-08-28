package io.github.tanice.terraCraft.bukkit.command.gem;

import io.github.tanice.terraCraft.api.item.component.TerraGemComponent;
import io.github.tanice.terraCraft.api.item.component.TerraGemHolderComponent;
import io.github.tanice.terraCraft.api.item.component.TerraInnerNameComponent;
import io.github.tanice.terraCraft.bukkit.command.CommandRunner;
import io.github.tanice.terraCraft.bukkit.item.component.GemComponent;
import io.github.tanice.terraCraft.bukkit.item.component.GemHolderComponent;
import io.github.tanice.terraCraft.bukkit.item.component.TerraNameComponent;
import io.github.tanice.terraCraft.bukkit.util.MiniMessageUtil;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.github.tanice.terraCraft.api.command.TerraCommand.*;

public class DismantleCommand extends CommandRunner {

    @Override
    public String getName() {
        return "dismantle";
    }

    @Override
    public String getDescription() {
        return "dismantle a gem from the item in main hand, and able to ignore dismantle chance or dismantle failed loss";
    }

    @Override
    public String getUsage() {
        return """
                dismantle
                dismantle <ignore_chance>
                dismantle <gem_name>
                dismantle <gem_name> <ignore_chance>
                """;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(RED + "This command can only be executed by players");
            return true;
        }
        if (args.length > 2) {
            sender.sendMessage(RED + "Invalid number of arguments");
            return true;
        }
        ItemStack mainhandItem = player.getInventory().getItemInMainHand();
        TerraGemHolderComponent gemHolderComponent = GemHolderComponent.from(mainhandItem);
        if (gemHolderComponent == null) {
            sender.sendMessage(RED + "This item does not have a gem holder component");
            return true;
        }
        List<ItemStack> gems = gemHolderComponent.getGems();
        if (gems.isEmpty()) {
            sender.sendMessage(GOLD + "This item has no gems to remove");
            return true;
        }

        String gemName = null;
        boolean ignoreChance = false;
        // 处理参数组合
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("true") || args[0].equalsIgnoreCase("false")) {
                ignoreChance = Boolean.parseBoolean(args[0]);
            } else gemName = args[0];
        } else if (args.length == 2) {
            gemName = args[0];
            ignoreChance = Boolean.parseBoolean(args[1]);
        }

        ItemStack targetGem = null;
        if (gemName != null) {
            for (ItemStack gem : gems) {
                if (getGemId(gem).equals(gemName)) {
                    targetGem = gem;
                    break;
                }
            }
            if (targetGem == null) {
                player.sendMessage(RED + "No gem found with name: " + gemName);
                return true;
            }
        } else targetGem = gems.getFirst();

        TerraGemComponent gemComponent = GemComponent.from(targetGem);
        if (gemComponent == null) ignoreChance = true;
        /* 成功 */
        if (ignoreChance || Math.random() < gemComponent.getInlaySuccessChance()) {
            if (gems.remove(targetGem)) {
                gemHolderComponent.setGems(gems);
                gemHolderComponent.cover(mainhandItem);
                targetGem.setAmount(1);
                player.getInventory().addItem(targetGem);
                player.updateInventory();
                sender.sendMessage(GREEN + "Removed gem successfully");
            } else {
                sender.sendMessage(RED + "Failed to remove gem");
                TerraCraftLogger.error("Player: " + player.getName() + " attempted to remove a null gem");
                return true;
            }
        /* 失败 */
        } else {
            String res = RED + "Dismantle failed";
            if (gemComponent.isDismantleFailLoss()) res += ", gem disappeared";
            player.sendMessage(res);
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            TerraGemHolderComponent gemHolderComponent = GemHolderComponent.from(((Player) sender).getInventory().getItemInMainHand());
            if (gemHolderComponent == null) return List.of();
            List<String> res = new ArrayList<>();
            for (ItemStack item : gemHolderComponent.getGems()) res.add(getGemId(item));
            res.add("true");
            res.add("false");
            return res;
        }
        if (args.length == 2) return List.of("true", "false");

        return Collections.emptyList();
    }

    private String getGemId(ItemStack gem) {
        TerraInnerNameComponent nameComponent = TerraNameComponent.from(gem);
        return nameComponent == null ? MiniMessageUtil.toNBTJson(gem.displayName()) : nameComponent.getName();
    }
}
