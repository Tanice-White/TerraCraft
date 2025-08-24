package io.github.tanice.terraCraft.bukkit.commands.buffs;

import io.github.tanice.terraCraft.bukkit.commands.CommandGroup;
import org.bukkit.plugin.java.JavaPlugin;

public class BuffGroupCommand extends CommandGroup {

    public BuffGroupCommand(JavaPlugin plugin) {
        super(plugin);
        this.register(new GiveCommand());
    }

    @Override
    public String getName() {
        return "buff";
    }
}
