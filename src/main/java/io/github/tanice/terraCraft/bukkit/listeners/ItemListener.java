package io.github.tanice.terraCraft.bukkit.listeners;

import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.TerraItemManager;
import io.github.tanice.terraCraft.api.items.gems.TerraGemHolder;
import io.github.tanice.terraCraft.api.players.TerraPlayerDataManager;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.events.TerraItemUpdateEvent;
import io.github.tanice.terraCraft.bukkit.utils.adapter.TerraBukkitAdapter;
import io.github.tanice.terraCraft.bukkit.utils.events.TerraEvents;
import io.github.tanice.terraCraft.bukkit.utils.pdc.PDCAPI;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Random;

public class ItemListener {
    private final Random random;

    public ItemListener() {
        random = new Random();

        /* 可食用物 */
        TerraEvents.subscribe(PlayerInteractEvent.class).priority(EventPriority.HIGH).ignoreCancelled(true).handler(event -> {

            if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

            ItemStack item = event.getItem();
            TerraBaseItem baseItem = TerraBukkitAdapter.itemAdapt(item);
            if (!(baseItem instanceof TerraEdible edible)) return;

            Player player = event.getPlayer();
            TerraPlayerDataManager playerDataManager = TerraCraftBukkit.inst().getPlayerDataManager();

            playerDataManager.getPlayerData(player.getUniqueId())
                    .ifPresentOrElse(playerData -> {
                        int currentTimes = playerData.getAte().get(edible.getName());
                        if (edible.getTimes() < 0 || edible.getTimes() > currentTimes) {
                            playerData.getAte().put(edible.getName(), currentTimes + 1);
                            if (edible.apply(player)) {
                                item.setAmount(item.getAmount() - 1);
                                player.setCooldown(item, edible.getCd());
                            }
                        } else {
                            player.sendMessage("§c有股神秘的力量在阻止你吃下它");
                        }
                    }, () -> TerraCraftLogger.error("Player data not found for UUID: " + player.getUniqueId()));
            event.setCancelled(true);
        }).register();

        /* 宝石镶嵌 物品升级 */
        TerraEvents.subscribe(InventoryClickEvent.class).priority(EventPriority.HIGH).ignoreCancelled(true).handler(event -> {
            /* 检查是否是工作台界面 左键点击 */
            if (event.getInventory().getType() != InventoryType.WORKBENCH || event.getClick() != ClickType.LEFT) return;
            if (!(event.getWhoClicked() instanceof Player player)) return;
            /* 排除非工作台的9个槽位 */
            InventoryView view = event.getView();
            Inventory clickedInventory = event.getClickedInventory();
            if (clickedInventory != view.getTopInventory() || event.getSlot() < 1 || event.getSlot() > 9) return;

            /* 获取光标物品和被点击物品 */
            ItemStack cursorItem = event.getCursor(), clickedItem = event.getCurrentItem();
            TerraBaseItem cursorBaseItem = TerraBukkitAdapter.itemAdapt(cursorItem);
            TerraBaseItem clickedBaseItem = TerraBukkitAdapter.itemAdapt(clickedItem);

            /* 宝石镶嵌 */
            if (cursorBaseItem instanceof TerraGem gem && clickedBaseItem instanceof TerraGemHolder gemCarrier) {
                int limit = gemCarrier.getGemStackNumber();
                String[] gems = PDCAPI.getGems(clickedItem);
                if ((gems == null && limit > 0) || (gems != null && gems.length < limit)) {
                    /* 镶嵌 */
                    if (random.nextDouble() > gem.getChance()) {
                        String res = "§c镶嵌失败";
                        if (gem.lossWhenFailed()) {
                            cursorItem.setAmount(cursorItem.getAmount() - 1);
                            res += ", 宝石消失";
                        }
                        player.sendMessage(res);
                    } else {
                        String[] newGems;
                        if (gems == null) newGems = new String[]{gem.getName()};
                        else {
                            newGems = Arrays.copyOf(gems, gems.length + 1);
                            newGems[gems.length] = gem.getName();
                        }
                        PDCAPI.setGems(clickedItem, newGems);
                        cursorItem.setAmount(cursorItem.getAmount() - 1);
                        player.sendMessage("§a镶嵌成功!");
                        TerraEvents.call(new TerraItemUpdateEvent(player, clickedBaseItem, clickedItem));
                    }
                }
                player.sendMessage("§e此物品无法镶嵌");
                /* 取消后续默认操作 */
                event.setCancelled(true);
                return;
            }

            /* 物品升级 */
            if (clickedBaseItem instanceof TerraLeveled leveled && cursorBaseItem != null) {
                TerraCraftBukkit.inst().getItemManager().getLevelTemplate(leveled.getLevelTemplateName()).ifPresent(template -> {
                    /* 是否是升级材料 */
                    if (!template.getMaterial().equals(cursorBaseItem.getName())) return;

                    Integer lvl = PDCAPI.getLevel(clickedItem);
                    if (lvl == null) lvl = 0;
                    lvl = Math.max(lvl, template.getBegin());
                    lvl = Math.min(lvl, template.getMax());

                    if (lvl == template.getMax()) {
                        player.sendMessage("§e等级已达到上限");
                        return;
                    }
                    /* 升级 */
                    if (random.nextDouble() > template.getChance()) {
                        String res = "§c强化失败";
                        if (template.isFailedLevelDown()) {
                            PDCAPI.setLevel(clickedItem, Math.max(lvl - 1, template.getBegin()));
                            res += ", 物品降级";
                            TerraEvents.call(new TerraItemUpdateEvent(player, clickedBaseItem, clickedItem));
                        }
                        player.sendMessage(res);
                    } else {
                        PDCAPI.setLevel(clickedItem, lvl + 1);
                        player.sendMessage("§a强化成功!");
                        TerraEvents.call(new TerraItemUpdateEvent(player, clickedBaseItem, clickedItem));
                    }
                    cursorItem.setAmount(cursorItem.getAmount() - 1);
                });
            }
            event.setCancelled(true);
        }).register();

        // TODO 物品重铸需要使用指令

        /* 物品更新 */
        TerraEvents.subscribe(TerraItemUpdateEvent.class).priority(EventPriority.MONITOR).handler(event -> {
            TerraItemManager itemManager = TerraCraftBukkit.inst().getItemManager();

            ItemStack pre = event.getItemStack();
            /* hash不等则先更新底层 */
            if (PDCAPI.getCode(pre) != event.getTerraBaseItem().getHashCode()) {
                TerraBaseItem baseItem = event.getTerraBaseItem();
                Player player = event.getPlayer();;
                for (String gemName : baseItem.selfUpdate(pre))
                    itemManager.getItem(gemName).ifPresentOrElse(gem -> {
                        player.getInventory().addItem(gem.getBukkitItem().clone());
                    }, () -> {
                        TerraCraftLogger.warning("Gem " + gemName + " does not exist when updating item: " + pre.getItemMeta().getDisplayName() + "for player" + player.getName());
                        player.sendMessage("§c物品: " + pre.getItemMeta().getDisplayName() + "更新，宝石获取错误，请联系管理员");
                    });
            }
            // TODO 更新lore

        }).register();
    }
}
