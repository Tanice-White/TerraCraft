package io.github.tanice.terraCraft.bukkit.command;

import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import org.bukkit.command.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.util.*;

import static io.github.tanice.terraCraft.api.command.TerraCommand.*;

public class TerraCraftCommand extends CommandGroup implements CommandExecutor, TabCompleter {

    public TerraCraftCommand(JavaPlugin plugin) {
        super(plugin);
    }

    public void enable() {
        PluginCommand command = plugin.getCommand("terracraft");
        if (command == null) {
            TerraCraftLogger.error("Main command not found");
            return;
        }
        command.setExecutor(this);
        command.setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, String[] args) {
        return this.execute(sender, args);
    }

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, String[] args) {
        return this.tabComplete(sender, args);
    }

    @Override
    protected void sendHelp(CommandSender sender) {
        sender.sendMessage(GOLD + "=== TerraCraft Help ===");
        subCommands.values().forEach(cmd ->
                sender.sendMessage(String.format(GRAY + "/%s " + AQUA + "%s " + WHITE + "- %s", cmd.getName(), cmd.getUsage(), cmd.getDescription()))
        );
    }

    @Override
    public String getName() {
        return "terracraft";
    }

    @Override
    public String getPermission() {
        return "terracraft.command";
    }
}