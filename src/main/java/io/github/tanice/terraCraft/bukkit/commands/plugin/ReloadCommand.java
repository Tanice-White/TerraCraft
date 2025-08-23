package io.github.tanice.terraCraft.bukkit.commands.plugin;

import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.commands.SubCommand;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends SubCommand {
    @Override
    public String getName() {
        return "reload";
    }

    public String getDescription() {
        return "reload terracraft";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        TerraCraftBukkit.inst().reload();
        return true;
    }
}
