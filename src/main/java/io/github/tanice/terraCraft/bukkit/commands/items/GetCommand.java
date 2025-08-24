package io.github.tanice.terraCraft.bukkit.commands.items;

import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.commands.CommandRunner;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.github.tanice.terraCraft.api.commands.TerraCommand.*;

public class GetCommand extends CommandRunner {
    @Override
    public String getName() {
        return "get";
    }

    @Override
    public String getDescription() {
        return "give a TerraCraft item to the target player";
    }

    @Override
    public String getUsage() {
        return """
                get <item> [amount]
                get <item> [player] [amount]
                """;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(RED + "This command can only be executed by players");
            return true;
        }
        if (args.length == 0) {
            List<String> items = TerraCraftBukkit.inst().getItemManager().filterItems("").stream().sorted().toList();
            String title = "所有物品 (" + items.size() + " 个)";
            sender.sendMessage(GOLD + BOLD + "=== " + title + " ===");
            items.forEach(item -> sender.sendMessage(GRAY + "- " + YELLOW + item));
            return true;
        }

        String itemName = args[0];
        List<String> matches = TerraCraftBukkit.inst().getItemManager().filterItems(itemName).stream().sorted().toList();
        if (matches.isEmpty()) {
            player.sendMessage(RED + "没有找到匹配 '" + itemName + "' 的物品");
            return true;
        }
        if (matches.size() > 1) {
            String title = "匹配 '" + itemName + "' 的物品 (" + matches.size() + " 个)";
            sender.sendMessage(GOLD + BOLD + "=== " + title + " ===");
            matches.forEach(item -> sender.sendMessage(GRAY + "- " + YELLOW + item));
            return true;
        }
        Player targetPlayer = player;
        int amount = 1;
        if (args.length >= 2) {
            // 尝试解析第二个参数为数量
            try {
                amount = Math.max(1, Integer.parseInt(args[1]));
            } catch (NumberFormatException ignored1) {
                // 第二个参数为玩家
                targetPlayer = Bukkit.getPlayer(args[1]);
                if (targetPlayer == null) {
                    sender.sendMessage(RED + "玩家: " + args[1] + " 不存在");
                    return true;
                }
                if (args.length >= 3) {
                    try {
                        amount = Math.max(1, Integer.parseInt(args[2]));
                    } catch (NumberFormatException ignored2) {
                    }
                }
            }
        }
        // 执行给予物品操作
        Optional<TerraBaseItem> item = TerraCraftBukkit.inst().getItemManager().getItem(itemName);
        if (item.isEmpty()) sender.sendMessage(RED + "无法生成物品: " + itemName);
        else {
            ItemStack giveItem = item.get().getBukkitItem().clone();
            giveItem.setAmount(amount);
            targetPlayer.getInventory().addItem(giveItem);
            sender.sendMessage(String.format("%s成功给予 %s %s%d个%s物品: %s%s", GREEN, targetPlayer.getName(), YELLOW, amount, GREEN, BLUE, itemName));
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) return TerraCraftBukkit.inst().getItemManager().filterItems(args[0]).stream().sorted().toList();;

        if (args.length == 2) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.startsWith(args[1]))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
