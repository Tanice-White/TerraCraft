package io.github.tanice.terraCraft.bukkit.listener.helper;

import io.github.tanice.terraCraft.api.listener.TerraListener;
import io.github.tanice.terraCraft.api.skill.TerraSkillManager;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.util.nbtapi.TerraNBTAPI;
import io.github.tanice.terraCraft.core.config.ConfigManager;
import io.github.tanice.terraCraft.core.util.helper.mythicmobs.TerraDamageMechanic;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class MythicListener implements Listener, TerraListener {
    public MythicListener() {
        TerraCraftBukkit.inst().getServer().getPluginManager().registerEvents(this, TerraCraftBukkit.inst());
    }

    @Override
    public void reload() {

    }

    @Override
    public void unload() {

    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onMythicMechanicLoad(MythicMechanicLoadEvent event) {
        if (event.getMechanicName().equalsIgnoreCase("terraDamage") || event.getEventName().equalsIgnoreCase("td")) {
            event.register(new TerraDamageMechanic(event.getConfig()));
        }
    }

    // ================== Mana ==================
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Mana恢复速度
        if (TerraNBTAPI.getManaRecoverySpeed(player) < 0) TerraNBTAPI.setManaRecoverySpeed(player, ConfigManager.getOriginalManaRecoverySpeed());
        // Mana值初始化
        TerraCraftBukkit.inst().getSkillManager().setPlayerMana(player, TerraNBTAPI.getMana(player));
        // Mana最大值
        if (TerraNBTAPI.getMana(player) < 0 ) TerraNBTAPI.setMana(player, ConfigManager.getOriginalMaxMana());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        // mana值写回
        Player player = event.getPlayer();
        TerraNBTAPI.setMana(player, TerraCraftBukkit.inst().getSkillManager().getPlayerMana(player));
    }

    // ================== Trigger ==================
    /* 下蹲 */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        TerraSkillManager.Trigger trigger = player.isSneaking() ? TerraSkillManager.Trigger.CROUCH_UP : TerraSkillManager.Trigger.CROUCH_DOWN;
        TerraCraftBukkit.inst().getSkillManager().castSkill(player, trigger);
    }

    /* 玩家释放技能（右键/左键等交互） */
    /* 如果忽略已经取消的事件则导致 左右键空气的时候不生效 */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player player = event.getPlayer();
        TerraSkillManager.Trigger trigger;
        if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            if (!TerraNBTAPI.isOnGround(player) && !player.isSwimming()) {
                trigger = TerraSkillManager.Trigger.JUMP_LEFT;
            } else {
                trigger = player.isSneaking() ? TerraSkillManager.Trigger.CROUCH_LEFT : TerraSkillManager.Trigger.LEFT_CLICK;
            }
        } else {
            if (!TerraNBTAPI.isOnGround(player) && !player.isSwimming()) {
                trigger = TerraSkillManager.Trigger.JUMP_RIGHT;
            } else {
                trigger = player.isSneaking() ? TerraSkillManager.Trigger.CROUCH_RIGHT : TerraSkillManager.Trigger.RIGHT_CLICK;
            }
        }
        TerraCraftBukkit.inst().getSkillManager().castSkill(player, trigger);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onShoot(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player player) {
            TerraCraftBukkit.inst().getSkillManager().castSkill(player, TerraSkillManager.Trigger.SHOOT);
        }
    }
    // TODO 根据 mmWiki拓展监听器
}
