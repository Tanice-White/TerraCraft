package io.github.tanice.terraCraft.bukkit.listeners;

import io.github.tanice.terraCraft.bukkit.utils.events.TerraEvents;
import io.github.tanice.terraCraft.core.calculator.DamageCalculator;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import org.bukkit.entity.*;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.projectiles.ProjectileSource;

public class DamageListener {

    public DamageListener() {

        /* 原版无源伤害 */
        TerraEvents.subscribe(EntityDamageEvent.class).priority(EventPriority.HIGH).ignoreCancelled(true).handler(event -> {
            if (event instanceof EntityDamageByEntityEvent) return;
            TerraCraftLogger.info("defender: " + event.getEntity().getName() + event.getFinalDamage());
        }).register();

        /* 原版有源伤害 */
        TerraEvents.subscribe(EntityDamageByEntityEvent.class).priority(EventPriority.HIGH).ignoreCancelled(true).handler(event -> {
            Entity entityAttacker = event.getDamager();
            Entity entityDefender = event.getEntity();
            /* 防御方必须是生物 */
            if (!(entityDefender instanceof LivingEntity defender)) return;

            /* 来源是生物 */
            // TODO damage(damageNumber, attacker) 会无线循环
            if (entityAttacker instanceof LivingEntity attacker) {
                double d = DamageCalculator.calculate(attacker, defender, event.getDamage()).getFinalDamage();
                defender.damage(d);
            }
            /* 抛射物情况 */
            else if (entityAttacker instanceof Projectile projectile) {
                ProjectileSource source = projectile.getShooter();
                /* 实体发射的 箭 或者 三叉戟 或者 烟花火箭 */
                if ((projectile instanceof Arrow || projectile instanceof Trident || projectile instanceof Firework) && source instanceof LivingEntity attacker) {
                    double d = DamageCalculator.calculate(attacker, defender, event.getDamage()).getFinalDamage();
                    defender.damage(d);
                }
            }
            /* 仅仅是伤害 */
            defender.damage(DamageCalculator.calculate(defender, event.getDamage()).getFinalDamage());

            event.setCancelled(true);
        }).register();
    }

    public void reload() {

    }

    public void unload() {

    }
}
