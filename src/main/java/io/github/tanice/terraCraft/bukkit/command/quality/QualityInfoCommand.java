package io.github.tanice.terraCraft.bukkit.command.quality;

import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.command.CommandRunner;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

import static io.github.tanice.terraCraft.api.command.TerraCommand.*;

public class QualityInfoCommand extends CommandRunner {

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "check the default config of the quality";
    }

    @Override
    public String getUsage() {
        return "info <quality_name>";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(RED + "Invalid number of arguments");
            return true;
        }
        TerraCraftBukkit.inst().getItemManager().getQuality(args[0]).ifPresentOrElse(quality -> {
            sender.sendMessage(quality.toString());
        }, () -> sender.sendMessage(RED + "Invalid quality quality name"));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return TerraCraftBukkit.inst().getItemManager().filterQualities(args[0]).stream().toList();
        }
        return Collections.emptyList();
    }
}
