package io.github.tanice.terraCraft.bukkit.commands.buffs;

import io.github.tanice.terraCraft.bukkit.commands.SubCommand;
import org.bukkit.command.CommandSender;

public class PlayerBuffsCommand extends SubCommand {
    @Override
    public String getName() {
        return "buffs";
    }

    @Override
    public String getDescription() {
        return "get TerraCraft buffs information of a player";
    }

    @Override
    public String getUsage() {
        return "give <buff> #[duration] :[interval] [player]";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        return false;
    }
}
