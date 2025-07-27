package io.github.tanice.terraCraft.bukkit.events.entity;

import io.github.tanice.terraCraft.api.attribute.AttributeType;
import io.github.tanice.terraCraft.api.attribute.DamageFromType;
import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import io.github.tanice.terraCraft.api.attribute.calculator.TerraAttributeCalculator;
import io.github.tanice.terraCraft.api.buffs.BuffActiveCondition;
import io.github.tanice.terraCraft.api.buffs.TerraBuffManager;
import io.github.tanice.terraCraft.api.buffs.TerraRunnableBuff;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.TerraItem;
import io.github.tanice.terraCraft.api.plugin.TerraPlugin;
import io.github.tanice.terraCraft.api.utils.js.TerraJSEngineManager;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.items.Item;
import io.github.tanice.terraCraft.bukkit.utils.adapter.BukkitItemAdapter;
import io.github.tanice.terraCraft.bukkit.utils.annotation.NonnullByDefault;
import io.github.tanice.terraCraft.bukkit.utils.EquipmentUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

@NonnullByDefault
public class TerraEntityDamageByEntityEvent extends TerraEntityDamageEvent {
    /** 攻击方 */

    protected LivingEntity attacker;

    public TerraEntityDamageByEntityEvent(LivingEntity attacker, LivingEntity defender, double oriDamage, boolean critical) {
        super(defender, oriDamage);
        this.attacker = attacker;
        this.critical = critical;
    }

    public double getFinalDamage() {
        TerraPlugin plugin = TerraCraftBukkit.inst();
        TerraJSEngineManager jsEngine = plugin.getJSEngineManager();

        TerraAttributeCalculator attackerCalculator = plugin.getEntityAttributeManager().getAttributeCalculator(attacker);
        TerraAttributeCalculator defenderCalculator = plugin.getEntityAttributeManager().getAttributeCalculator(defender);
        TerraCalculableMeta attackerMeta = attackerCalculator.getMeta();
        TerraCalculableMeta defenderMeta = defenderCalculator.getMeta();

        /* 计算玩家生效属性 */
        /* 非法属性都在OTHER中 */
        DamageFromType weaponDamageType = DamageFromType.OTHER;
        boolean isTerraItem = false;

        EntityEquipment equipment = attacker.getEquipment();
        if (equipment != null) {
            ItemStack mainHandItem = equipment.getItemInMainHand();
            TerraBaseItem bit = BukkitItemAdapter.itemAdapt(mainHandItem);
            if (bit instanceof Item it) {
                weaponDamageType = it.getDamageType();
                isTerraItem = true;
            }
        }

        double finalDamage;

        Object damageAttributes = new DamageAttributes(attacker, defender, 0, attackerAttrMods, defenderAttrMods, weaponDamageType);
        /* BEFORE_DAMAGE 事件计算 */
        List<TerraRunnableBuff> bd = attackerCalculator.getOrderedBeforeList(BuffActiveCondition.ATTACKER);
        bd.addAll(defenderCalculator.getOrderedBeforeList(BuffActiveCondition.DEFENDER));
        Collections.sort(bd);
        boolean answer;
        for (TerraRunnableBuff buff : bd) {
            answer = jsEngine.executeFunction(buff.getFileName(), damageAttributes);
            /* 可能被更改 */
            finalDamage = damageAttributes.getDamage();
            if (!answer) return finalDamage;
        }

        /* 武器的对外白值（品质+宝石+白值） */
        finalDamage = attackerMeta.get(AttributeType.ATTACK_DAMAGE);
        /* 不影响原版武器伤害的任何计算 */
        /* 原版弓箭伤害就是会飘 */
        if (!isTerraItem) finalDamage += damage;
            /* 怪物拿起的武器不受这个影响，伤害是满额的 */
            /* 所以玩家才计算比例 */
        else if (attacker instanceof Player p) finalDamage *= 0.15 + p.getAttackCooldown() * 0.85;

        finalDamage *= 1 + attackerCalculator.getMeta().get(weaponDamageType);
        if (critical) finalDamage *= 1 + Config.originalCriticalStrikeAddition;

        /* 暴击 */
        critical = false;
        if (rand.nextDouble() < attackerMeta.get(AttributeType.CRITICAL_STRIKE_CHANCE)){
            critical = true;
            finalDamage *= attackerMeta.get(AttributeType.CRITICAL_STRIKE_DAMAGE) < 1 ? 1: attackerMeta.get(AttributeType.CRITICAL_STRIKE_DAMAGE);
        }

        /* 伤害浮动 */
        if (damageFloat) finalDamage *= rand.nextDouble(1 - floatRange, 1 + floatRange);

        damageAttributes.setDamage(finalDamage);

        /* 中间属性生效 */
        List<TerraRunnableBuff> be = attackerCalculator.getOrderedBetweenList(BuffActiveCondition.ATTACKER);
        be.addAll(defenderCalculator.getOrderedBetweenList(BuffActiveCondition.DEFENDER));
        Collections.sort(be);
        for (TerraRunnableBuff buff : be) {
            answer = jsEngine.executeFunction(buff.getFileName(), damageAttributes);
            /* 可能被更改 */
            finalDamage = damageAttributes.getDamage();
            if (!answer) return finalDamage;
        }

        /* 最终伤害计算 */
        finalDamage *= (1 - defenderMeta.get(AttributeType.PRE_ARMOR_REDUCTION));
        finalDamage -= defenderMeta.get(AttributeType.ARMOR) * worldK;
        /* 数值修正 */
        finalDamage = Math.max(0, finalDamage);
        finalDamage *= (1 - defenderMeta.get(AttributeType.AFTER_ARMOR_REDUCTION));

        /* AFTER_DAMAGE 事件计算 */
        damageAttributes.setDamage(finalDamage);

        List<TerraRunnableBuff> ad = attackerCalculator.getOrderedAfterList(BuffActiveCondition.ATTACKER);
        ad.addAll(defenderCalculator.getOrderedAfterList(BuffActiveCondition.DEFENDER));
        Collections.sort(ad);
        for (TerraRunnableBuff buff : ad) {
            answer = jsEngine.executeFunction(buff.getFileName(), damageAttributes);
            /* 可能被更改 */
            finalDamage = damageAttributes.getDamage();
            if (!answer) return finalDamage;
        }

        this.activateBuffForAttackerAndDefender(attacker, defender);

        return finalDamage;
    }

    /**
     * 为攻击方和防御方激活相关 buff
     */
    protected void activateBuffForAttackerAndDefender(LivingEntity attacker, LivingEntity defender) {
        TerraBuffManager buffManager = TerraCraftBukkit.inst().getBuffManager();
        /* attacker 给 defender 增加 buff */
        boolean fa = false, fb = false;
        for (TerraItem i : EquipmentUtil.getActiveEquipmentItem(attacker)){
            fa |= buffManager.activateBuffs(attacker, i.getAttackBuffsForSelf());
            fb |= buffManager.activateBuffs(defender, i.getAttackBuffsForOther());
        }
        /* defender 给 attacker 增加 buff */
        for (TerraItem i : EquipmentUtil.getActiveEquipmentItem(defender)){
            fb |= buffManager.activateBuffs(defender, i.getDefenseBuffsForSelf());
            fa |= buffManager.activateBuffs(attacker, i.getDefenseBuffsForOther());
        }
        if (fa) Bukkit.getPluginManager().callEvent(new TerraAttributeUpdateEvent(attacker));
        if (fb) Bukkit.getPluginManager().callEvent(new TerraAttributeUpdateEvent(defender));
    }
}
