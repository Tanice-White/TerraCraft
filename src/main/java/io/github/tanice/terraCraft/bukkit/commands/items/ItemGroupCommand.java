package io.github.tanice.terraCraft.bukkit.commands.items;

import io.github.tanice.terraCraft.bukkit.commands.CommandGroup;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemGroupCommand extends CommandGroup {

    public ItemGroupCommand(JavaPlugin plugin) {
        super(plugin);
        this.register(new GetCommand());
    }

    @Override
    public String getDescription() {
        return "Command related to terracraft items";
    }

    @Override
    public String getName() {
        return "item";
    }
}
