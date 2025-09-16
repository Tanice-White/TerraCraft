package io.github.tanice.terraCraft.bukkit.command.attribute;

import io.github.tanice.terraCraft.bukkit.command.CommandGroup;
import org.bukkit.plugin.java.JavaPlugin;

public class AttributeGroupCommand extends CommandGroup {
    public AttributeGroupCommand(JavaPlugin plugin) {
        super(plugin);
        this.register(new PlayerAttributeGetCommand());
        this.register(new PlayerAttributeSetCommand());
    }

    @Override
    public String getDescription() {
        return "Command related to player's attributes";
    }

    @Override
    public String getName() {
        return "attribute";
    }
}
