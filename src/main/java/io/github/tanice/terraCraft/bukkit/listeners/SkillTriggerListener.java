package io.github.tanice.terraCraft.bukkit.listeners;

import io.github.tanice.terraCraft.api.skills.TerraSkillManager;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.utils.events.TerraEvents;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class SkillTriggerListener {

    public SkillTriggerListener() {
        /* 下蹲 */
        TerraEvents.subscribe(PlayerToggleSneakEvent.class).priority(EventPriority.HIGH).ignoreCancelled(true).handler(event -> {
            Player player = event.getPlayer();
            TerraSkillManager.Trigger trigger = player.isSneaking() ? TerraSkillManager.Trigger.CROUCH_UP : TerraSkillManager.Trigger.CROUCH_DOWN;
            TerraCraftBukkit.inst().getSkillManager().castSkill(player, trigger);
        }).register();

        /* 玩家释放技能（右键/左键等交互） */
        TerraEvents.subscribe(PlayerInteractEvent.class).priority(EventPriority.HIGH).ignoreCancelled(true).handler(event -> {
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
        }).register();

        /* 弓箭离弦 */
        TerraEvents.subscribe(EntityShootBowEvent.class).priority(EventPriority.HIGH).ignoreCancelled(true).handler(event -> {
            LivingEntity entity = event.getEntity();
            if (entity instanceof Player player) {
                TerraCraftBukkit.inst().getSkillManager().castSkill(player, TerraSkillManager.Trigger.SHOOT);
            }
        }).register();
    }
}
