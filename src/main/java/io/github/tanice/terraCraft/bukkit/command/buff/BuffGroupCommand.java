package io.github.tanice.terraCraft.bukkit.command.buff;

import io.github.tanice.terraCraft.bukkit.command.CommandGroup;
import org.bukkit.plugin.java.JavaPlugin;

public class BuffGroupCommand extends CommandGroup {

    public BuffGroupCommand(JavaPlugin plugin) {
        super(plugin);
        this.register(new GiveCommand());
        this.register(new BuffInfoCommand());
        this.register(new PlayerBuffsGetCommand());
        this.register(new ClearCommand());
    }

    @Override
    public String getDescription() {
        return "Command related to terracraft buffs";
    }

    @Override
    public String getName() {
        return "buff";
    }
}
