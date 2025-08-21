package io.github.tanice.terraCraft.bukkit.commands.item;

import io.github.tanice.terraCraft.api.items.TerraItemManager;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.commands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GetCommand extends SubCommand {
    @Override
    public String getName() {
        return "get";
    }

    @Override
    public String getDescription() {
        return "get TerraCraft items";
    }

    @Override
    public String getUsage() {
        return "\nget <item> <[empty]|amount>\nget <item> <player> <[empty]|amount>";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(RED + "Player only");
            return true;
        }

        if (args.length == 0) {
            showItemList(player, "");
            return true;
        }

        String itemName = args[0];
        List<String> matches = getMatchingItems(itemName);

        if (matches.isEmpty()) {
            player.sendMessage(RED + "没有找到匹配 '" + itemName + "' 的物品");
            return true;
        }
        if (matches.size() > 1) {
            showItemList(player, itemName);
            return true;
        }

        int nextArgIndex = 1;
        Player targetPlayer = parseTargetPlayer(args, nextArgIndex, player);

        if (targetPlayer != null && !targetPlayer.getName().equals(player.getName())) {
            nextArgIndex++;
        }
        int amount = parseAmount(args, nextArgIndex);

        giveItem(player, itemName, targetPlayer, amount);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) return getMatchingItems(args[0]);

        if (args.length == 2) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.startsWith(args[1]))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    /**
     * 给予玩家指定数量的物品
     */
    private void giveItem(CommandSender sender, String itemName, Player targetPlayer, int amount) {
        TerraItemManager itemManager = TerraCraftBukkit.inst().getItemManager();
        itemManager.getItem(itemName).ifPresentOrElse(item -> {
            ItemStack giveItem = item.getBukkitItem().clone();
            giveItem.setAmount(amount);
            targetPlayer.getInventory().addItem(giveItem);
            sender.sendMessage(String.format("%s成功给予 %s %s%d个%s物品: %s%s", GREEN, targetPlayer.getName(), YELLOW, amount, GREEN, BLUE, itemName));
        }, () -> sender.sendMessage(RED + "无法生成物品: " + itemName));
    }

    /**
     * 显示匹配的物品列表
     */
    private void showItemList(CommandSender sender, String filter) {
        List<String> items = getMatchingItems(filter);

        if (items.isEmpty()) {
            sender.sendMessage(filter.isEmpty() ? RED + "当前没有可用物品" : RED + "没有匹配 '" + filter + "' 的物品");
            return;
        }
        String title = filter.isEmpty() ? "所有物品 (" + items.size() + " 个)" : "匹配 '" + filter + "' 的物品 (" + items.size() + " 个)";
        sender.sendMessage(GOLD + BOLD + "=== " + title + " ===");
        items.forEach(item -> sender.sendMessage(GRAY + "- " + YELLOW + item));
    }

    /**
     * 获取匹配的物品列表
     */
    private List<String> getMatchingItems(String filter) {
        return TerraCraftBukkit.inst().getItemManager().filterItems(filter).stream().sorted().toList();
    }
}
