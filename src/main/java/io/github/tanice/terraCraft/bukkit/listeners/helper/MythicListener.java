package io.github.tanice.terraCraft.bukkit.listeners.helper;

import io.github.tanice.terraCraft.api.skills.TerraSkillManager;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.core.utils.helper.mythicmobs.TerraDamageMechanic;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class MythicListener implements Listener {

    public MythicListener() {
        TerraCraftBukkit.inst().getServer().getPluginManager().registerEvents(this, TerraCraftBukkit.inst());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onMythicMechanicLoad(MythicMechanicLoadEvent event) {
        if (event.getMechanicName().equalsIgnoreCase("terraDamage") || event.getEventName().equalsIgnoreCase("td")) {
            event.register(new TerraDamageMechanic(event.getConfig()));
        }
    }

    /* 下蹲 */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        TerraSkillManager.Trigger trigger = player.isSneaking() ? TerraSkillManager.Trigger.CROUCH_UP : TerraSkillManager.Trigger.CROUCH_DOWN;
        TerraCraftBukkit.inst().getSkillManager().castSkill(player, trigger);
    }

    /* 玩家释放技能（右键/左键等交互） */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player player = event.getPlayer();
        TerraSkillManager.Trigger trigger;

        if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            if (!player.isOnGround() && !player.isSwimming()) {
                trigger = TerraSkillManager.Trigger.JUMP_LEFT;
            } else {
                trigger = player.isSneaking() ? TerraSkillManager.Trigger.CROUCH_LEFT : TerraSkillManager.Trigger.LEFT_CLICK;
            }
        } else {
            if (!player.isOnGround() && !player.isSwimming()) {
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

    public void reload() {

    }

    public void unload() {

    }
}
