package io.github.tanice.terraCraft.bukkit.command.buff;

import io.github.tanice.terraCraft.api.buff.TerraBaseBuff;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.command.CommandRunner;
import io.github.tanice.terraCraft.bukkit.util.nbtapi.NBTBuff;
import io.github.tanice.terraCraft.core.buff.impl.TimerBuff;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

import static io.github.tanice.terraCraft.api.command.TerraCommand.*;

public class GiveCommand extends CommandRunner {
    @Override
    public String getName() {
        return "give";
    }

    @Override
    public String getDescription() {
        return "give the target player a TerraCraft buff";
    }

    @Override
    public String getUsage() {
        return "give <buff> #[duration] :[interval] [player]";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 1 || args.length > 4) {
            sender.sendMessage(RED + "Invalid number of arguments");
            return true;
        }
        Player target = null;
        if (sender instanceof Player p) target = p;

        StringBuilder str = new StringBuilder(args[0]);
        for (int i = 1; i < args.length; i++) {
            if (args[i].startsWith("#")) {
                try {
                    Integer.parseInt(args[i].substring(1));
                    str.append(" ").append(args[i]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(RED + "Invalid duration number");
                    return true;
                }
            } else if (args[i].startsWith(":")) {
                try {
                    Integer.parseInt(args[i].substring(1));
                    str.append(" ").append(args[i]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(RED + "Invalid interval number");
                    return true;
                }
            } else {
                Player foundPlayer = Bukkit.getPlayer(args[i]);
                if (foundPlayer == null || !foundPlayer.isOnline()) {
                    sender.sendMessage(RED + "Player: " + args[1] + " does not exist");
                    return true;
                }
                target = foundPlayer;
            }
        }

        if (target == null) {
            sender.sendMessage(RED + "Player: " + args[1] + " does not exist");
            return true;
        }

        NBTBuff nbtBuff = new NBTBuff(str.toString());
        TerraBaseBuff buff = nbtBuff.getAsTerraBuff();
        if (buff == null) {
            sender.sendMessage(RED + "Invalid buff");
            return true;
        }
        buff.setChance(1);
        TerraCraftBukkit.inst().getBuffManager().activateBuff(target, buff);
        sender.sendMessage(String.format(
                GREEN + "Successfully gave buff " + GOLD + "%s" + GREEN + " to " + YELLOW + "%s" + GREEN +
                        " Duration: " + AQUA + "%d" + GREEN + "s, Interval: " + AQUA + "%s" + GREEN + "s",
                buff.getName(),
                target.getName(),
                buff.getDuration(),
                buff instanceof TimerBuff r ? r.getCd() : "None"
        ));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length > 1) return playerList(args[args.length - 1]);
        if (args.length > 0) return TerraCraftBukkit.inst().getBuffManager().filterBuffs(args[0]).stream().toList();
        return Collections.emptyList();
    }
}
