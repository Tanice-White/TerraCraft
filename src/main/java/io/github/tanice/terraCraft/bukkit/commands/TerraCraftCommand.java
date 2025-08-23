package io.github.tanice.terraCraft.bukkit.commands;

import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

import static io.github.tanice.terraCraft.api.commands.TerraCommand.*;

public class TerraCraftCommand implements CommandExecutor, TabCompleter {
    private final JavaPlugin plugin;
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public TerraCraftCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void onload() {
        PluginCommand command = plugin.getCommand("terracraft");
        if (command == null) {
            TerraCraftLogger.error("Main command not found");
            return;
        }
        command.setExecutor(this);
        command.setTabCompleter(this);
        subCommands.values().stream().filter(subCommand -> subCommand instanceof Listener).forEach(
                subCommand -> Bukkit.getPluginManager().registerEvents((Listener) subCommand, plugin)
        );
        subCommands.values().forEach(SubCommand::onload);
        plugin.getLogger().info("Load " + subCommands.size() + " Commands");
    }

    public void reload() {
        subCommands.values().forEach(SubCommand::reload);
    }

    public void unload() {
        subCommands.values().forEach(SubCommand::unload);
    }

    public void register(SubCommand command) {
        subCommands.put(command.getName().toLowerCase(), command);
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        SubCommand subCommand = subCommands.get(args[0].toLowerCase());
        if (subCommand == null) {
            sender.sendMessage(RED + "Unknown subcommand! Available commands:");

            List<String> availableCommands = subCommands.values().stream()
                    .filter(c -> c.hasPermission(sender))
                    .map(SubCommand::getName)
                    .sorted()
                    .collect(Collectors.toList());
            if (availableCommands.isEmpty()) {
                sender.sendMessage(RED + "You don't have permission for any subcommands!");

            } else sender.sendMessage(GREEN + String.join(GRAY + "," + GREEN, availableCommands));
            return true;
        }

        if (!subCommand.hasPermission(sender)) {
            sender.sendMessage(RED + "You don't have permission to execute this command!");
            sender.sendMessage(RED + "Required permission: " + YELLOW + subCommand.getPermission());
            return true;
        }

        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        return subCommand.execute(sender, subArgs);
    }

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, String[] args) {
        if (args.length == 1) {
            return subCommands.values().stream()
                    .filter(c -> c.hasPermission(sender))
                    .map(SubCommand::getName)
                    .filter(name -> name.startsWith(args[0].toLowerCase()))
                    .sorted()
                    .collect(Collectors.toList());
        }

        SubCommand subCommand = subCommands.get(args[0].toLowerCase());
        if (subCommand != null) {
            String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
            return subCommand.tabComplete(sender, subArgs);
        }
        return Collections.emptyList();
    }

    private void sendHelp(@Nonnull CommandSender sender) {
        sender.sendMessage(GOLD + "=== TerraCraft Help ===");
        subCommands.values().forEach(cmd ->
                sender.sendMessage(String.format(GRAY + "/%s " + AQUA + "%s " + WHITE + "- %s", cmd.getName(), cmd.getUsage(), cmd.getDescription()))
        );
    }
}