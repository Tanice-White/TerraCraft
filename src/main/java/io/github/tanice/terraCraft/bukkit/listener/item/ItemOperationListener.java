package io.github.tanice.terraCraft.bukkit.listener.item;

import io.github.tanice.terraCraft.api.item.component.TerraGemComponent;
import io.github.tanice.terraCraft.api.item.component.TerraGemHolderComponent;
import io.github.tanice.terraCraft.api.item.component.TerraInnerNameComponent;
import io.github.tanice.terraCraft.api.item.component.TerraLevelComponent;
import io.github.tanice.terraCraft.api.listener.TerraListener;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.event.item.TerraItemUpdateEvent;
import io.github.tanice.terraCraft.bukkit.item.component.*;
import io.github.tanice.terraCraft.bukkit.util.event.TerraEvents;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class ItemOperationListener implements Listener, TerraListener {

    private final Random random = new Random();

    public ItemOperationListener() {
        TerraCraftBukkit.inst().getServer().getPluginManager().registerEvents(this, TerraCraftBukkit.inst());
    }

    @Override
    public void reload() {

    }

    @Override
    public void unload() {

    }

    /* 宝石镶嵌 物品升级 */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        /* 检查是否是工作台界面 左键点击 */
        if (event.getInventory().getType() != InventoryType.WORKBENCH || event.getClick() != ClickType.LEFT) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;
        /* 排除非工作台的9个槽位 */
        InventoryView view = event.getView();
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory != view.getTopInventory() || event.getSlot() < 1 || event.getSlot() > 9) return;
        /* 获取光标物品和被点击物品 */
        ItemStack cursorItem = event.getCursor(), clickedItem = event.getCurrentItem();
        if (cursorItem.isEmpty() || clickedItem == null) return;

        /* 宝石镶嵌 */
        ItemStack preClicked = clickedItem.clone();
        TerraGemComponent gemComponent = GemComponent.from(cursorItem);
        TerraGemHolderComponent holderComponent = GemHolderComponent.from(clickedItem);
        TerraInnerNameComponent nameComponent = TerraNameComponent.from(clickedItem);
        if (gemComponent != null && holderComponent != null && nameComponent != null) {
            List<ItemStack> gems = holderComponent.getGems();
            int limit = holderComponent.getLimit();
            String res;
            if (limit > gems.size()) {
                /* 镶嵌成功 */
                if (random.nextDouble() < gemComponent.getInlaySuccessChance()) {
                    cursorItem.setAmount(cursorItem.getAmount() - 1);
                    gems.add(cursorItem);
                    res = "§a镶嵌成功";
                    /* nbt回写 */
                    holderComponent.apply(clickedItem);
                    TerraEvents.call(new TerraItemUpdateEvent(player, nameComponent.getName(), preClicked, clickedItem));
                } else {
                    /* 失败消耗 */
                    res = "§c镶嵌失败";
                    if (gemComponent.isInlayFailLoss()) {
                        cursorItem.setAmount(cursorItem.getAmount() - 1);
                        res += ", 宝石消失";
                    }
                }
                player.sendMessage(res);
            } else player.sendMessage("§c宝石槽位已满");
            /* 取消后续默认操作 */
            event.setCancelled(true);
            return;
        }

        /* 物品升级 */
        TerraLevelComponent levelComponent = LevelComponent.from(clickedItem);
        TerraNameComponent clickedNameComponent = TerraNameComponent.from(clickedItem);
        TerraInnerNameComponent cursorNameComponent = TerraNameComponent.from(cursorItem);
        if (levelComponent != null && cursorNameComponent != null && clickedNameComponent != null) {
            TerraCraftBukkit.inst().getItemManager().getLevelTemplate(levelComponent.getTemplate()).ifPresent(template -> {
                if (!template.getMaterial().equals(cursorNameComponent.getName())) return;

                int lvl = levelComponent.getLevel();
                cursorItem.setAmount(cursorItem.getAmount() - 1);
                String res;
                if (lvl < template.getMax() - template.getBegin()) {
                    if (random.nextDouble() < template.getChance()) {
                        res = "§a强化成功";
                        levelComponent.setLevel(lvl + 1);
                        levelComponent.apply(clickedItem);
                        TerraEvents.call(new TerraItemUpdateEvent(player, clickedNameComponent.getName(), preClicked, clickedItem));
                    } else {
                        res = "§c强化失败";
                        if (template.isFailedLevelDown() && lvl > 0) {
                            res += ", 物品降级";
                            levelComponent.setLevel(lvl - 1);
                            levelComponent.apply(clickedItem);
                            TerraEvents.call(new TerraItemUpdateEvent(player, clickedNameComponent.getName(), preClicked, clickedItem));
                        }
                    }
                    player.sendMessage(res);
                } else player.sendMessage("§c物品已达最大等级");
                event.setCancelled(true);
            });
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onItemConsume(PlayerItemConsumeEvent event) {
        /* 原版食物效果自动生效, 额外效果由指令替代 */
        CommandComponent component = CommandComponent.from(event.getItem());
        if (component == null) return;
        List<String> commands = component.getCommands();
        String playerName = event.getPlayer().getName();
        for (String command : commands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("@self", playerName));
        }
    }
}
