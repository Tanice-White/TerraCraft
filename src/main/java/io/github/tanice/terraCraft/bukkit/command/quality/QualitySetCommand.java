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
import java.util.Collections;
import java.util.List;

import static io.github.tanice.terraCraft.api.command.TerraCommand.*;

public class QualitySetCommand extends CommandRunner {

    @Override
    public String getName() {
        return "set";
    }

    @Override
    public String getDescription() {
        return "set the quality of the mainhand item";
    }

    @Override
    public String getUsage() {
        return "set <quality_name>";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length != 1) {
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

        for (TerraQuality q : getQualities(qualityComponent)) {
            if (q.getName().equals(args[0])) {
                qualityComponent.setQuality(q.getName());
                QualityComponent.clear(item);
                qualityComponent.apply(item);
                sender.sendMessage(GREEN + "quality recast to " + YELLOW + q.getDisplayName() +RESET);
                return true;
            }
        }
        sender.sendMessage(RED + "Invalid quality name in item groups");
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length != 1) return Collections.emptyList();
        if (!(sender instanceof Player player)) return Collections.emptyList();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.isEmpty()) return Collections.emptyList();
        TerraQualityComponent qualityComponent = QualityComponent.from(item);
        if (qualityComponent == null) return Collections.emptyList();
        return getQualities(qualityComponent).stream().map(TerraQuality::getName).toList();
    }

    private List<TerraQuality> getQualities(TerraQualityComponent component) {
        TerraQualityGroup group = new QualityGroup("tmp", new ArrayList<>(0));
        for (String gs : component.getGroups()) {
            TerraCraftBukkit.inst().getItemManager().getQualityGroup(gs).ifPresent(group::merge);
        }
        return group.getQualities();
    }
}
