package io.github.tanice.terraCraft.bukkit.command.plugin;

import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.command.CommandRunner;
import io.github.tanice.terraCraft.bukkit.event.TerraCraftReloadEvent;
import io.github.tanice.terraCraft.bukkit.util.event.TerraEvents;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends CommandRunner {
    @Override
    public String getName() {
        return "reload";
    }

    public String getDescription() {
        return "reload terracraft";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        TerraEvents.call(new TerraCraftReloadEvent());
        TerraCraftBukkit.inst().reload();
        return true;
    }
}
