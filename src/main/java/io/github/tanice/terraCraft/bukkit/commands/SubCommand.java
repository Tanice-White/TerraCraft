package io.github.tanice.terraCraft.bukkit.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public abstract class SubCommand {

    protected static final String BLACK = "§0";             // 黑色
    protected static final String DARK_BLUE = "§1";         // 深蓝色
    protected static final String DARK_GREEN = "§2";        // 深绿色
    protected static final String DARK_AQUA = "§3";         // 深青色
    protected static final String DARK_RED = "§4";          // 深红色
    protected static final String DARK_PURPLE = "§5";       // 深紫色
    protected static final String GOLD = "§6";              // 金色
    protected static final String GRAY = "§7";              // 灰色
    protected static final String DARK_GRAY = "§8";         // 深灰色
    protected static final String BLUE = "§9";              // 蓝色
    protected static final String GREEN = "§a";             // 绿色
    protected static final String AQUA = "§b";              // 青色
    protected static final String RED = "§c";               // 红色
    protected static final String LIGHT_PURPLE = "§d";      // 粉红色
    protected static final String YELLOW = "§e";            // 黄色
    protected static final String WHITE = "§f";             // 白色

    protected static final String OBFUSCATED = "§k";        // 模糊/乱码
    protected static final String BOLD = "§l";              // 粗体
    protected static final String STRIKETHROUGH = "§m";     // 删除线
    protected static final String UNDERLINE = "§n";         // 下划线
    protected static final String ITALIC = "§o";            // 斜体
    protected static final String RESET = "§r";             // 重置格式

    public abstract String getName();

    public String getDescription() {
        return "";
    }

    public String getUsage() {
        return "";
    }

    public String getPermission() {
        return "terracraft.command." + getName().toLowerCase();
    }

    public abstract boolean execute(CommandSender sender, String[] args);

    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    public void onload(){}

    public void unload(){}

    public void reload(){}

    protected boolean hasPermission(CommandSender sender) {
        return sender.hasPermission(getPermission());
    }

    /**
     * 解析目标玩家
     */
    protected Player parseTargetPlayer(String[] args, int index, Player defaultPlayer) {
        if (args.length > index) {
            Player target = Bukkit.getPlayer(args[index]);
            return target != null ? target : defaultPlayer;
        }
        return defaultPlayer;
    }

    /**
     * 解析物品数量
     */
    protected int parseAmount(String[] args, int index) {
        if (args.length > index) {
            try {
                int amount = Integer.parseInt(args[index]);
                return Math.max(1, amount);
            } catch (NumberFormatException ignore) {
            }
        }
        return 1;
    }
}