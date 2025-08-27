package io.github.tanice.terraCraft.bukkit.command.gem;

import io.github.tanice.terraCraft.bukkit.command.CommandGroup;
import org.bukkit.plugin.java.JavaPlugin;

public class GemGroupCommand extends CommandGroup {
    public GemGroupCommand(JavaPlugin plugin) {
        super(plugin);
        this.register(new InlayCommand());
        this.register(new DismantleCommand());
    }

    @Override
    public String getDescription() {
        return "Command related to terracraft gems";
    }

    @Override
    public String getName() {
        return "gem";
    }
}
