package io.github.tanice.terraCraft.bukkit.commands;

import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends SubCommand{
    @Override
    public String getName() {
        return "reload";
    }

    public String getDescription() {
        return "重载插件物品";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        TerraCraftBukkit.inst().reload();
        return true;
    }
}
