package io.github.tanice.terraCraft.bukkit.listener;

import io.github.tanice.terraCraft.api.attribute.AttributeType;
import io.github.tanice.terraCraft.api.buff.TerraBuffManager;
import io.github.tanice.terraCraft.api.listener.TerraListener;
import io.github.tanice.terraCraft.api.protocol.TerraDamageProtocol;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.item.component.BuffComponent;
import io.github.tanice.terraCraft.bukkit.util.EquipmentUtil;
import io.github.tanice.terraCraft.bukkit.util.nbtapi.vanilla.NBTBuff;
import io.github.tanice.terraCraft.bukkit.util.scheduler.TerraSchedulers;
import io.github.tanice.terraCraft.core.calculator.DamageCalculator;
import io.github.tanice.terraCraft.core.config.ConfigManager;
import io.github.tanice.terraCraft.core.util.logger.TerraCraftLogger;
import io.github.tanice.terraCraft.core.util.registry.Registry;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import javax.annotation.Nullable;
import java.util.Objects;

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
        if (!ConfigManager.isDebug()) return;
        TerraSchedulers.sync().runLater(() -> {
            TerraCraftLogger.debug(TerraCraftLogger.DebugLevel.CALCULATOR, "defender: " + event.getEntity().getName() + " " + event.getFinalDamage());
        }, 1);
    }

    /* 原版有源伤害 */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entityAttacker = event.getDamager();
        Entity entityDefender = event.getEntity();
        TerraDamageProtocol protocol;
        /* 忽略荆棘伤害 */
        if (event.getDamageSource().getDamageType() == DamageType.THROWN) return;
        if (!(entityDefender instanceof LivingEntity defender)) return;
        /* 来源是生物 */
        if (entityAttacker instanceof LivingEntity attacker) {
            protocol = DamageCalculator.calculate(
                    attacker,
                    defender,
                    attacker instanceof Player ? 0 : event.getDamage(),
                    event.isCritical(),
                    true);
            event.setDamage(Math.max(1, protocol.getFinalDamage()));  // 配合后面的护盾防御检测
            if (!protocol.isHit()) event.setCancelled(true);
        /* 抛射物 */
        } else if (entityAttacker instanceof Projectile projectile) {
            ProjectileSource source = projectile.getShooter();
            /* 生物发射的 xx箭 三叉戟 烟花火箭 烈焰弹 */
            if ((projectile instanceof AbstractArrow || projectile instanceof Firework || projectile instanceof Fireball) && source instanceof LivingEntity attacker) {
                /* 三叉戟有meta需要单独计算 */
                protocol = DamageCalculator.calculate(
                        attacker,
                        defender,
                        projectile instanceof Trident
                                ? (attacker instanceof Player ? 0 : event.getDamage() - Registry.ORI_ITEM.get("trident").get(AttributeType.ATTACK_DAMAGE))
                                : event.getDamage(),
                        false,
                        false);
                event.setDamage(Math.max(1, protocol.getFinalDamage()));  // 配合后面的护盾防御检测
                if (!protocol.isHit()) event.setCancelled(true);
            }
        } else {
            // 铁砧下落等伤害
            protocol = DamageCalculator.calculate(defender, event.getDamage(), false, false);
            event.setDamage(Math.max(1, protocol.getFinalDamage()));  // 配合后面的护盾防御检测
            if (!protocol.isHit()) event.setCancelled(true);
        }
    }

    /* 最后实体互相上buff */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void afterEntityDamageByEntity(EntityDamageByEntityEvent event) {
        /* 忽略荆棘伤害 */
        if (event.getDamageSource().getDamageType() == DamageType.THROWN) return;
        /* 防御方必须是实体 */
        if (!(event.getEntity() instanceof LivingEntity defender)) return;
        if (event.getDamager() instanceof LivingEntity attacker) activateBuffForAttackerAndDefender(attacker, defender);
        else if (event.getDamager() instanceof Projectile projectile) {
            ProjectileSource source = projectile.getShooter();
            if ((projectile instanceof AbstractArrow || projectile instanceof Firework || projectile instanceof Fireball) && source instanceof LivingEntity attacker) {
                activateBuffForAttackerAndDefender(attacker, defender);
            }
        }
    }

    private static void activateBuffForAttackerAndDefender(@Nullable LivingEntity attacker, LivingEntity defender) {
        TerraBuffManager buffManager = TerraCraftBukkit.inst().getBuffManager();
        /* attacker 给 defender 增加 buff */
        BuffComponent buffComponent;
        if (attacker != null) {
            for (ItemStack item : EquipmentUtil.getActiveEquipmentItemStack(attacker)){
                buffComponent = BuffComponent.from(item);
                if (buffComponent == null) continue;
                buffManager.activateBuffs(attacker, buffComponent.getAttackSelf().stream().map(NBTBuff::getAsTerraBuff).filter(Objects::nonNull).toList());
                buffManager.activateBuffs(defender, buffComponent.getAttack().stream().map(NBTBuff::getAsTerraBuff).filter(Objects::nonNull).toList());
            }
        }
        /* defender 给 attacker 增加 buff */
        for (ItemStack item : EquipmentUtil.getActiveEquipmentItemStack(defender)){
            buffComponent = BuffComponent.from(item);
            if (buffComponent == null) continue;
            buffManager.activateBuffs(defender, buffComponent.getDefenseSelf().stream().map(NBTBuff::getAsTerraBuff).filter(Objects::nonNull).toList());
            if (attacker != null) buffManager.activateBuffs(attacker, buffComponent.getDefense().stream().map(NBTBuff::getAsTerraBuff).filter(Objects::nonNull).toList());
        }
    }
}
