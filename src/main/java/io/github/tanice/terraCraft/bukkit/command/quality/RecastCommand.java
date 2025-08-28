package io.github.tanice.terraCraft.bukkit.command.quality;

import io.github.tanice.terraCraft.api.item.component.TerraQualityComponent;
import io.github.tanice.terraCraft.api.item.quality.TerraQuality;
import io.github.tanice.terraCraft.api.item.quality.TerraQualityGroup;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.command.CommandRunner;
import io.github.tanice.terraCraft.bukkit.item.component.QualityComponent;
import io.github.tanice.terraCraft.core.item.quality.QualityGroup;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

import static io.github.tanice.terraCraft.api.command.TerraCommand.*;

public class RecastCommand extends CommandRunner {

    @Override
    public String getName() {
        return "recast";
    }

    @Override
    public String getDescription() {
        return "recast the quality of the mainhand item";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length != 0) {
            sender.sendMessage(RED + "Invalid number of arguments");
            return true;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage(RED + "This command can only be executed by players");
            return true;
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.isEmpty()) {
            sender.sendMessage(RED + "Please hold terra item in mainhand");
            return true;
        }
        TerraQualityComponent qualityComponent = QualityComponent.from(item);
        if (qualityComponent == null) {
            sender.sendMessage(GOLD + "Item does not have quality component");
            return true;
        }

        TerraQualityGroup group = new QualityGroup("tmp", new ArrayList<>(0));
        for (String gs : qualityComponent.getGroups()) {
            TerraCraftBukkit.inst().getItemManager().getQualityGroup(gs).ifPresent(group::merge);
        }
        TerraQuality quality = group.randomSelect();
        String qn = quality == null ? null : quality.getName();
        qualityComponent.setQuality(qn);
        qualityComponent.cover(item);
        sender.sendMessage(GREEN + "quality recast to " + YELLOW + (qn == null ? "null" : qn) +RESET);
        return true;
    }
}