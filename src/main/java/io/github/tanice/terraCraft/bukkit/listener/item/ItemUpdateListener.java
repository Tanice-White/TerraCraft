package io.github.tanice.terraCraft.bukkit.listener.item;

import io.github.tanice.terraCraft.api.item.component.TerraInnerNameComponent;
import io.github.tanice.terraCraft.api.item.component.TerraUpdateCodeComponent;
import io.github.tanice.terraCraft.api.listener.TerraListener;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.event.item.TerraItemUpdateEvent;
import io.github.tanice.terraCraft.bukkit.item.component.TerraNameComponent;
import io.github.tanice.terraCraft.bukkit.item.component.UpdateCodeComponent;
import io.github.tanice.terraCraft.bukkit.util.event.TerraEvents;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemUpdateListener implements Listener, TerraListener {
    private final List<Player> checkPlayers = new ArrayList<>();

    public ItemUpdateListener() {
        Bukkit.getScheduler().runTaskTimer(TerraCraftBukkit.inst(), () -> {
            if (checkPlayers.isEmpty()) return;
            List<Player> checkPlayers = new ArrayList<>(this.checkPlayers);
            this.checkPlayers.clear();
            for (Player player : checkPlayers) {
                if (player != null) checkAndUpdateItem(player, player.getInventory().getContents());
            }
        }, 20, 20);
        TerraCraftBukkit.inst().getServer().getPluginManager().registerEvents(this, TerraCraftBukkit.inst());
    }

    @Override
    public void reload() {

    }

    @Override
    public void unload() {

    }

    @EventHandler
    void onPlayerHeld(PlayerItemHeldEvent event) {
        Inventory inv = event.getPlayer().getInventory();
        checkAndUpdateItem(event.getPlayer(), inv.getItem(event.getPreviousSlot()), inv.getItem(event.getNewSlot()));
    }

    @EventHandler
    void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getPlayer() instanceof Player player && player.equals(event.getInventory().getHolder())) {
            checkPlayers.add(player);
        }
    }

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event) {
        checkPlayers.add(event.getPlayer());
    }

    /**
     * 物品更新
     */
    private void checkAndUpdateItem(Player player, ItemStack... itemStacks) {
        for (ItemStack item : itemStacks) {
            if (item == null || item.isEmpty()) continue;
            TerraUpdateCodeComponent codeComponent = UpdateCodeComponent.from(item);
            TerraInnerNameComponent nameComponent = TerraNameComponent.from(item);
            if (codeComponent == null || nameComponent == null) continue;
            TerraCraftBukkit.inst().getItemManager().getItem(nameComponent.getName()).ifPresent(baseItem -> {
                ItemStack preItem = item.clone();
                if (baseItem.updateOld(item)) {
                    player.updateInventory();
                    TerraEvents.call(new TerraItemUpdateEvent(player, nameComponent.getName(), preItem, item));
                }
            });
            return;
        }
    }
}
