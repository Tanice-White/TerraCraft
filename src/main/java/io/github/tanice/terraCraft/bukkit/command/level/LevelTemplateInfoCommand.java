package io.github.tanice.terraCraft.bukkit.command.level;

import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.command.CommandRunner;
import org.bukkit.command.CommandSender;

import java.util.List;

import static io.github.tanice.terraCraft.api.command.TerraCommand.*;

public class LevelTemplateInfoCommand extends CommandRunner {

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "check the default config of the level template";
    }

    @Override
    public String getUsage() {
        return "info <template_name>";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length > 1) {
            sender.sendMessage(RED + "Invalid number of arguments");
            return true;
        }
        TerraCraftBukkit.inst().getItemManager().getLevelTemplate(args[0])
                .ifPresentOrElse(tmp -> sender.sendMessage(tmp.toString()), () -> sender.sendMessage(RED + "Invalid level template name"));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return TerraCraftBukkit.inst().getItemManager().filterTemplates(args[0]).stream().toList();
    }
}
