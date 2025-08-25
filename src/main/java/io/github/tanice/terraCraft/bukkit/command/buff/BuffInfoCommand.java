package io.github.tanice.terraCraft.bukkit.command.buff;

import io.github.tanice.terraCraft.api.buff.TerraBuffManager;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.command.CommandRunner;
import org.bukkit.command.CommandSender;

import java.util.List;

import static io.github.tanice.terraCraft.api.command.TerraCommand.*;
import static io.github.tanice.terraCraft.api.command.TerraCommand.YELLOW;

public class BuffInfoCommand extends CommandRunner {
    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "check the default config of the buff";
    }

    @Override
    public String getUsage() {
        return "info <buff>";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        TerraBuffManager buffManager = TerraCraftBukkit.inst().getBuffManager();
        if (args.length == 0) {
            List<String> buffs = buffManager.filterBuffs("").stream().sorted().toList();
            sender.sendMessage(GOLD + BOLD + "=== Total " + buffs.size() + " buffs ===");
            buffs.forEach(buff -> sender.sendMessage(GRAY + "- " + YELLOW + buff));
            return true;
        }

        String buff = args[0];
        List<String> matches = buffManager.filterBuffs(buff).stream().sorted().toList();
        if (matches.isEmpty()) {
            sender.sendMessage(RED + "No buff found matching '" + buff + "'");
            return true;
        }
        if (matches.size() > 1) {
            sender.sendMessage(GOLD + BOLD + "=== " + matches.size() + " buffs matching '" + buff + "' ===");
            matches.forEach(item -> sender.sendMessage(GRAY + "- " + YELLOW + item));
            return true;
        }
        sender.sendMessage(buffManager.getBuff(matches.get(0)).toString());
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return TerraCraftBukkit.inst().getBuffManager().filterBuffs(args[0]).stream().toList();
    }
}
