package io.github.tanice.terraCraft.bukkit.commands.buffs;

import io.github.tanice.terraCraft.api.buffs.TerraBaseBuff;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.commands.CommandRunner;
import io.github.tanice.terraCraft.bukkit.utils.nbtapi.NBTBuff;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static io.github.tanice.terraCraft.api.commands.TerraCommand.*;

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
        Player target = null;
        if (sender instanceof Player p) target = p;
        if (args.length < 1) {
            sender.sendMessage(RED + "Missing buff name");
            return true;
        }

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
                    sender.sendMessage(RED + "Player " + args[i] + " is not online!");
                    return true;
                }
                target = foundPlayer;
            }
        }

        if (target == null) {
            sender.sendMessage(RED + "Invalid target player");
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
                        "Duration: " + AQUA + "%d" + GREEN + "s, Interval: " + AQUA + "%d" + GREEN + "s",
                nbtBuff.getName(),
                target.getName(),
                nbtBuff.getDuration(),
                nbtBuff.getCd()
        ));
        return true;
    }
}
