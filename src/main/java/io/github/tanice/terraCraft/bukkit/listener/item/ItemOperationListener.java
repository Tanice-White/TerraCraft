package io.github.tanice.terraCraft.bukkit.listener.item;

import io.github.tanice.terraCraft.api.item.component.*;
import io.github.tanice.terraCraft.api.listener.TerraListener;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.item.component.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.*;
import org.bukkit.projectiles.ProjectileSource;

import javax.annotation.Nullable;
import java.util.List;

import static io.github.tanice.terraCraft.bukkit.util.nbtapi.TerraNBTAPI.*;

public class ItemOperationListener implements Listener, TerraListener {

    public ItemOperationListener() {
        TerraCraftBukkit.inst().getServer().getPluginManager().registerEvents(this, TerraCraftBukkit.inst());
    }

    @Override
    public void reload() {

    }

    @Override
    public void unload() {

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

    /* 耐久监听-射箭*/
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.getGameMode() == GameMode.CREATIVE) return;
        if (processDurability(player, event.getBow(), 1)) {
            ItemStack item = event.getConsumable();
            if (event.shouldConsumeItem() && item != null && !item.isEmpty())
                player.getInventory().addItem(item);
            event.setCancelled(true);
        }
    }

    /* 耐久监听-钓鱼 */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerReelIn(PlayerFishEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) return;
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH || event.getState() == PlayerFishEvent.State.CAUGHT_ENTITY) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (!item.isEmpty() && item.getType() == Material.FISHING_ROD) {
                if (processDurability(player, item, 1)) event.setCancelled(true);
            } else {
                item = player.getInventory().getItemInOffHand();
                if (!item.isEmpty() && item.getType() == Material.FISHING_ROD) {
                    if (processDurability(player, item, 1)) event.setCancelled(true);
                }
            }
        }
    }

    /* 耐久监听-方块交互 */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) return;
        /* 硬度为0不消耗耐久 */
        if (event.getBlock().getType().getHardness() == 0.0f) return;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (processDurability(player, item, damagePerBlock(item))) event.setCancelled(true);
    }

    /* 耐久监听-盾牌档伤害事件的优先级是high */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entityAttacker = event.getDamager();
        Entity entityDefender = event.getEntity();
        ItemStack item;
        /* 攻击方处理 */
        if (entityAttacker instanceof Player attacker) {
            if (attacker.getGameMode() != GameMode.CREATIVE) {
                item = attacker.getInventory().getItemInMainHand();
                /* 将伤害值为1 */
                if (processDurability(attacker, item, damagePerAttack(item))) event.setDamage(1);
            }
            /* 抛射物 */
        } else if (entityAttacker instanceof Projectile projectile) {
            ProjectileSource source = projectile.getShooter();
            if (source instanceof Player attacker && attacker.getGameMode() != GameMode.CREATIVE) {
                /* 只有三叉戟需要监听耐久 */
                if (projectile instanceof Trident trident && processDurability(attacker, trident.getItemStack(), damagePerAttack(trident.getItemStack()))) {
                    event.setCancelled(true);  /* 抛射物取消事件 */
                }
            }
        }
        /* 防御方处理 */
        if (entityDefender instanceof Player defender && defender.getGameMode() != GameMode.CREATIVE) {
            EntityEquipment equipment = defender.getEquipment();
            if (equipment == null) return;
            /* 护甲耐久减少 */
            for (ItemStack i : equipment.getArmorContents()) processDurability(defender, i, equipmentDamage(event.getFinalDamage()));
        }
    }

    /**
     * 处理耐久损失
     * @param item 需要丢失耐久的物品
     * @return 物品是否损坏--用于取消事件
     */
    private boolean processDurability(Player player, @Nullable ItemStack item, int damage) {
        if (item == null) return false;
        TerraDurabilityComponent component = DurabilityComponent.from(item);
        if (component == null) return false;
        /* 先判定是否已经损坏 */
        if (component.broken()) return true;
        component.setDamage(component.getDamage() + damage);
        component.cover(item);
        if (component.broken()) {
            // 播放break_sound
            player.playSound(player, breakSound(item), 1f, 1.2f);
            if (component.isBreakLoss()) item.setAmount(0);
            /* 本次可以使用 */
        }
        return false;
    }

    /**
     * 盾牌耐久计算
     * TODO 兼容原版耐久计算 使用 exp4j
     */
    private int blockDamage(double damage) {
        if (damage < 10) return 0;
        return (int) Math.round(damage + 1);
    }

    /**
     * 护甲耐久计算
     * TODO 兼容原版耐久计算 使用 exp4j
     */
    private int equipmentDamage(double damage) {
        return (int) Math.floor(Math.max(1, damage / 4));
    }
}
