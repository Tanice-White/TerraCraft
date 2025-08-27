package io.github.tanice.terraCraft.bukkit.command.quality;

import io.github.tanice.terraCraft.api.item.quality.TerraQuality;
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
        return "info <quality_group_name> <quality_name>";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(RED + "Invalid number of arguments");
            return true;
        }
        TerraCraftBukkit.inst().getItemManager().getQualityGroup(args[0]).ifPresentOrElse(group -> {
            for (TerraQuality quality : group.getQualities()) {
                if (quality.getName().equals(args[1])) {
                    sender.sendMessage(quality.toString());
                    return;
                }
            }
            sender.sendMessage(RED + "Invalid quality name");
        }, () -> sender.sendMessage(RED + "Invalid quality group name"));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return TerraCraftBukkit.inst().getItemManager().filterQualityGroups("").stream().toList();
        }
        if (args.length == 2) {
            return TerraCraftBukkit.inst().getItemManager().getQualityGroup(args[0])
                    .map(group -> group.getQualities().stream().map(TerraQuality::getName).toList())
                    .orElse(Collections.emptyList());
        }
        return Collections.emptyList();
    }
}
