package io.github.tanice.terraCraft.bukkit.command.quality;

import io.github.tanice.terraCraft.bukkit.command.CommandGroup;
import org.bukkit.plugin.java.JavaPlugin;

public class QualityGroupCommand extends CommandGroup {

    public QualityGroupCommand(JavaPlugin plugin) {
        super(plugin);
        this.register(new QualityInfoCommand());
        this.register(new RecastCommand());
        this.register(new RemoveCommand());
        this.register(new QualitySetCommand());
    }

    @Override
    public String getDescription() {
        return "Command related to terracraft item qualities";
    }

    @Override
    public String getName() {
        return "quality";
    }
}
