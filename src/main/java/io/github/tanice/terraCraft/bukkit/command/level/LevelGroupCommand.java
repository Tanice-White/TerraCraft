package io.github.tanice.terraCraft.bukkit.command.level;

import io.github.tanice.terraCraft.bukkit.command.CommandGroup;
import org.bukkit.plugin.java.JavaPlugin;

public class LevelGroupCommand extends CommandGroup {
    public LevelGroupCommand(JavaPlugin plugin) {
        super(plugin);
        this.register(new LevelUpCommand());
        this.register(new LevelDownCommand());
        this.register(new LevelTemplateInfoCommand());
    }

    @Override
    public String getDescription() {
        return "Command related to terracraft item levels";
    }

    @Override
    public String getName() {
        return "level";
    }
}
