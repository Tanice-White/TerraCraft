package io.github.tanice.terraCraft.bukkit.listeners;

import io.github.tanice.terraCraft.api.items.components.TerraGemComponent;
import io.github.tanice.terraCraft.api.items.components.TerraGemHolderComponent;
import io.github.tanice.terraCraft.api.items.components.TerraInnerNameComponent;
import io.github.tanice.terraCraft.api.items.components.TerraLevelComponent;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.items.components.*;
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

public class ItemListener implements Listener {

    private final Random random = new Random();

    public ItemListener() {
        TerraCraftBukkit.inst().getServer().getPluginManager().registerEvents(this, TerraCraftBukkit.inst());
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
        TerraGemComponent gemComponent = GemComponent.from(cursorItem);
        TerraGemHolderComponent holderComponent = GemHolderComponent.from(clickedItem);
        if (gemComponent != null && holderComponent != null ) {
            List<ItemStack> gems = holderComponent.getGems();
            int limit = holderComponent.getLimit();
            String res;
            if (limit > gems.size()) {
                /* 镶嵌成功 */
                if (random.nextDouble() < gemComponent.getInlaySuccessChance()) {
                    // TODO 物品更新事件, 如果事件未取消则向下执行
                    cursorItem.setAmount(cursorItem.getAmount() - 1);
                    gems.add(cursorItem);
                    res = "§a镶嵌成功";
                    /* nbt回写 */
                    holderComponent.apply(clickedItem);
                    // TODO 物品 lore更新
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
        TerraInnerNameComponent terraName = TerraNameComponent.from(cursorItem);
        if (levelComponent != null && terraName != null) {
            TerraCraftBukkit.inst().getItemManager().getLevelTemplate(levelComponent.getTemplate()).ifPresent(template -> {
                if (!template.getMaterial().equals(terraName.getName())) return;

                int lvl = levelComponent.getLevel();
                cursorItem.setAmount(cursorItem.getAmount() - 1);
                String res;
                if (lvl < template.getMax() - template.getBegin()) {
                    if (random.nextDouble() < template.getChance()) {
                        // TODO 物品更新事件, 如果事件未取消则向下执行
                        res = "§a强化成功";
                        levelComponent.setLevel(lvl + 1);
                        levelComponent.apply(clickedItem);
                        // TODO 物品 lore更新
                    } else {
                        res = "§c强化失败";
                        if (template.isFailedLevelDown() && lvl > 0) {
                            // TODO 物品更新事件, 如果事件未取消则向下执行
                            res += ", 物品降级";
                            levelComponent.setLevel(lvl - 1);
                            levelComponent.apply(clickedItem);
                            // TODO 物品 lore更新
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
        // TODO 非CustomData数据无法保存，需要单独储存在minecraft:customData中备份
        // TODO 设置UseCooldown, Consumable
        /* 原版食物效果自动生效, 额外效果由指令替代 */
        CommandsComponent component = CommandsComponent.from(event.getItem());
        if (component == null) return;
        List<String> commands = component.getCommands();
        String playerName = event.getPlayer().getName();
        for (String command : commands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("@self", playerName));
        }
    }

    public void reload() {

    }

    public void unload() {

    }
}
