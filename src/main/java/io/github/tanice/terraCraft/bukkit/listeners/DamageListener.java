package io.github.tanice.terraCraft.bukkit.listeners;

import io.github.tanice.terraCraft.bukkit.events.damage.TerraDamageEvent;
import io.github.tanice.terraCraft.bukkit.utils.events.TerraEvents;
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
            // TODO 直接生成指示器
        }).register();

        /* 原版有源伤害 */
        TerraEvents.subscribe(EntityDamageByEntityEvent.class).priority(EventPriority.HIGH).ignoreCancelled(true).handler(event -> {
            Entity entityAttacker = event.getDamager();
            Entity entityDefender = event.getEntity();

            /* 防御方必须是生物 */
            if (!(entityDefender instanceof LivingEntity defender)) return;

            /* 来源是生物 */
            if (entityAttacker instanceof LivingEntity attacker)
                // TODO 不用事件而是直接使用 Calculator 处理结果并直接damage(damageNumber, attacker)
                TerraEvents.call(new TerraDamageEvent(attacker, defender, false));
            /* 抛射物情况 */
            else if (entityAttacker instanceof Projectile projectile) {
                ProjectileSource source = projectile.getShooter();
                /* 实体发射的 箭 或者 三叉戟 或者 烟花火箭 */
                if ((projectile instanceof Arrow || projectile instanceof Trident || projectile instanceof Firework) && source instanceof LivingEntity attacker) {
                    // TODO 不用事件而是直接使用 Calculator 处理结果并直接damage(damageNumber, attacker)
                    TerraEvents.call(new TerraDamageEvent(attacker, defender, false));
                }
                event.setCancelled(true);
            }

        }).register();
    }

    public void reload() {

    }

    public void unload() {

    }
}
