package io.github.tanice.terraCraft.bukkit.listener;

import io.github.tanice.terraCraft.api.listener.TerraListener;
import io.github.tanice.terraCraft.api.protocol.TerraDamageProtocol;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.util.scheduler.TerraSchedulers;
import io.github.tanice.terraCraft.core.calculator.DamageCalculator;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.projectiles.ProjectileSource;

public class DamageListener implements Listener, TerraListener {

    public DamageListener() {
        TerraCraftBukkit.inst().getServer().getPluginManager().registerEvents(this, TerraCraftBukkit.inst());
    }

    @Override
    public void reload() {

    }

    @Override
    public void unload() {

    }

    /* 原版无源伤害 */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        TerraSchedulers.sync().runLater(() -> {
            TerraCraftLogger.info("defender: " + event.getEntity().getName() + " " + event.getFinalDamage());
        }, 1);
    }

    /* 原版有源伤害 */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entityAttacker = event.getDamager();
        Entity entityDefender = event.getEntity();
        TerraDamageProtocol protocol;
        if (!(entityDefender instanceof LivingEntity defender)) return;
        /* 来源是生物 */
        if (entityAttacker instanceof LivingEntity attacker) {
            protocol = DamageCalculator.calculate(attacker, defender, 0, event.isCritical(), true);
            if (protocol.isHit()) event.setDamage(Math.max(1, protocol.getFinalDamage()));
            else event.setCancelled(true);
        /* 抛射物 */
        } else if (entityAttacker instanceof Projectile projectile) {
            ProjectileSource source = projectile.getShooter();
            /* 实体发射的 xx箭 三叉戟 烟花火箭 烈焰弹 */
            if ((projectile instanceof AbstractArrow || projectile instanceof Firework || projectile instanceof Fireball) && source instanceof LivingEntity attacker) {
                /* 三叉戟有meta需要单独计算 */
                protocol = DamageCalculator.calculate(attacker, defender, projectile instanceof Trident ? 0 : event.getDamage(), false, false);
                if (protocol.isHit()) event.setDamage(Math.max(1, protocol.getFinalDamage()));
                else event.setCancelled(true);
            }
        } else {
            // 铁砧下落等伤害
            protocol = DamageCalculator.calculate(defender, event.getDamage(), false, false);
            if (protocol.isHit()) event.setDamage(Math.max(1, protocol.getFinalDamage()));
            else event.setCancelled(true);
        }
    }
}
