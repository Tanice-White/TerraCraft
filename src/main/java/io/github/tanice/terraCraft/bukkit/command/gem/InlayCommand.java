package io.github.tanice.terraCraft.bukkit.command.gem;

import io.github.tanice.terraCraft.api.item.component.TerraGemComponent;
import io.github.tanice.terraCraft.api.item.component.TerraGemHolderComponent;
import io.github.tanice.terraCraft.api.item.component.TerraInnerNameComponent;
import io.github.tanice.terraCraft.bukkit.command.CommandRunner;
import io.github.tanice.terraCraft.bukkit.event.item.TerraItemUpdateEvent;
import io.github.tanice.terraCraft.bukkit.item.component.GemComponent;
import io.github.tanice.terraCraft.bukkit.item.component.GemHolderComponent;
import io.github.tanice.terraCraft.bukkit.item.component.TerraNameComponent;
import io.github.tanice.terraCraft.bukkit.util.event.TerraEvents;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

import static io.github.tanice.terraCraft.api.command.TerraCommand.*;

public class InlayCommand extends CommandRunner {

    @Override
    public String getName() {
        return "insert";
    }

    @Override
    public String getUsage() {
        return """
                insert
                insert <ignore_chance>
                """;
    }

    @Override
    public String getDescription() {
        return "Inlay the gem in offhand to the item in main hand, and able to ignore inlay chance or inlay failed loss";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(RED + "This command can only be executed by players");
            return true;
        }
        if (args.length > 1) {
            sender.sendMessage(RED + "Invalid number of arguments");
            return true;
        }
        boolean ignoreChance = args.length > 0 && Boolean.parseBoolean(args[0]);

        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        ItemStack offHandItem = player.getInventory().getItemInOffHand();
        if (mainHandItem.isEmpty() || offHandItem.isEmpty()) {
            player.sendMessage(GOLD + "Please hold an item with gem_holder component in your main main hand and a gem in your off hand");
            return true;
        }
        ItemStack preClicked = mainHandItem.clone();
        TerraGemComponent gemComponent = GemComponent.from(offHandItem);
        TerraGemHolderComponent holderComponent = GemHolderComponent.from(mainHandItem);
        TerraInnerNameComponent nameComponent = TerraNameComponent.from(mainHandItem);
        if (gemComponent != null && holderComponent != null && nameComponent != null) {
            List<ItemStack> gems = holderComponent.getGems();
            int limit = holderComponent.getLimit();
            if (limit > gems.size()) {
                /* 成功 */
                if (ignoreChance || Math.random() < gemComponent.getInlaySuccessChance()) {
                    ItemStack tmp = offHandItem.clone();
                    tmp.setAmount(1);
                    gems.add(tmp);
                    offHandItem.setAmount(offHandItem.getAmount() - 1);
                    /* nbt回写 */
                    holderComponent.setGems(gems);
                    holderComponent.apply(mainHandItem);
                    TerraEvents.call(new TerraItemUpdateEvent(player, nameComponent.getName(), preClicked, mainHandItem));
                    player.sendMessage(GREEN + "Inlay successfully");
                /* 失败 */
                } else {
                    String res = RED + "Inlay failed";
                    if (gemComponent.isInlayFailLoss()) {
                        offHandItem.setAmount(offHandItem.getAmount() - 1);
                        res += ", gem disappeared";
                    }
                    player.sendMessage(res);
                }
            } else player.sendMessage(GOLD + "No more stack for gem");
            return true;
        }
        player.sendMessage(GOLD + "Please hold an item with gem_holder component in your main main hand and a gem in your off hand");
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) return List.of("true", "false");
        return Collections.emptyList();
    }
}
