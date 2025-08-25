package io.github.tanice.terraCraft.bukkit.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.stream.Collectors;

import static io.github.tanice.terraCraft.api.command.TerraCommand.*;

public abstract class CommandGroup extends CommandNode {
    protected final JavaPlugin plugin;
    protected final Map<String, CommandNode> subCommands;

    public CommandGroup(JavaPlugin plugin) {
        this.plugin = plugin;
        this.subCommands = new HashMap<>();
    }

    @Override
    public void onload() {
        subCommands.values().stream().filter(subCommand -> subCommand instanceof Listener).forEach(
                subCommand -> Bukkit.getPluginManager().registerEvents((Listener) subCommand, plugin)
        );
        subCommands.values().forEach(CommandNode::onload);
    }

    @Override
    public void reload() {
        subCommands.values().forEach(CommandNode::reload);
    }

    @Override
    public void unload() {
        subCommands.values().forEach(CommandNode::unload);
    }

    public void register(CommandNode node) {
        subCommands.put(node.getName().toLowerCase(), node);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        CommandNode node = subCommands.get(args[0].toLowerCase());
        if (node == null) {
            sender.sendMessage(RED + "Unknown subcommand! Available commands:");

            List<String> availableCommands = subCommands.values().stream()
                    .filter(c -> c.hasPermission(sender))
                    .map(CommandNode::getName)
                    .sorted()
                    .collect(Collectors.toList());
            if (availableCommands.isEmpty()) {
                sender.sendMessage(RED + "You don't have permission for any subcommands!");

            } else sender.sendMessage(GREEN + String.join(GRAY + "," + GREEN, availableCommands));
            return true;
        }

        if (!node.hasPermission(sender)) {
            sender.sendMessage(RED + "You don't have permission to execute this command!");
            sender.sendMessage(RED + "Required permission: " + YELLOW + node.getPermission());
            return true;
        }

        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        return node.execute(sender, subArgs);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return subCommands.values().stream()
                    .filter(c -> c.hasPermission(sender))
                    .map(CommandNode::getName)
                    .filter(name -> name.startsWith(args[0].toLowerCase()))
                    .sorted()
                    .collect(Collectors.toList());
        }

        CommandNode subCommand = subCommands.get(args[0].toLowerCase());
        if (subCommand != null) {
            String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
            return subCommand.tabComplete(sender, subArgs);
        }
        return Collections.emptyList();
    }

    // TODO 优化显示
    protected void sendHelp(CommandSender sender) {
        subCommands.values().forEach(cmd ->
                sender.sendMessage(String.format(GRAY + ".../%s\n" + AQUA + "%s " + WHITE + "- %s", cmd.getName(), cmd.getUsage(), cmd.getDescription()))
        );
    }
}
