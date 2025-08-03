package io.github.tanice.terraCraft.bukkit.events.damage;

import io.github.tanice.terraCraft.api.attribute.AttributeType;
import io.github.tanice.terraCraft.api.attribute.DamageFromType;
import io.github.tanice.terraCraft.api.attribute.calculator.TerraAttributeCalculator;
import io.github.tanice.terraCraft.api.buffs.BuffActiveCondition;
import io.github.tanice.terraCraft.api.buffs.TerraBuffManager;
import io.github.tanice.terraCraft.api.buffs.TerraRunnableBuff;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.TerraItem;
import io.github.tanice.terraCraft.api.plugin.TerraPlugin;
import io.github.tanice.terraCraft.api.protocol.TerraDamageProtocol;
import io.github.tanice.terraCraft.api.utils.js.TerraJSEngineManager;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.items.Item;
import io.github.tanice.terraCraft.bukkit.utils.EquipmentUtil;
import io.github.tanice.terraCraft.bukkit.utils.adapter.TerraBukkitAdapter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class aa {

    protected LivingEntity attacker;
    protected LivingEntity defender;
    double oriDamage;
    boolean isCritical;

    public double getFinalDamage() {
        TerraPlugin plugin = TerraCraftBukkit.inst();
        TerraJSEngineManager jsEngine = plugin.getJSEngineManager();
        TerraDamageProtocol context = initDamageContext(plugin);

        // 2. 前置Buff处理
        double finalDamage = processBeforeDamageBuffs(context, jsEngine);
        if (finalDamage >= 0) return finalDamage; // 若Buff中断流程，直接返回

        // 3. 计算基础伤害（含武器特性、攻击冷却等）
        finalDamage = calculateBaseDamage(context);

        // 4. 处理暴击和伤害浮动
        finalDamage = applyCriticalAndFloatDamage(context, finalDamage);
        context.damageAttributes.setDamage(finalDamage);

        // 5. 中间阶段Buff处理
        finalDamage = processBetweenDamageBuffs(context, jsEngine);
        if (finalDamage >= 0) return finalDamage;

        // 6. 应用防御方减免计算
        finalDamage = applyDefenseReductions(context, finalDamage);

        // 7. 后置Buff处理
        finalDamage = processAfterDamageBuffs(context, jsEngine);
        if (finalDamage >= 0) return finalDamage;

        // 8. 激活相关Buff
        activateBuffForAttackerAndDefender(context.attacker, context.defender);

        return finalDamage;
    }

    /**
     * 初始化伤害计算上下文（封装所有需要的参数）
     */
    private TerraDamageProtocol initDamageContext(TerraPlugin plugin) {
        TerraAttributeCalculator attackerCalculator = plugin.getEntityAttributeManager().getAttributeCalculator(attacker);
        TerraAttributeCalculator defenderCalculator = plugin.getEntityAttributeManager().getAttributeCalculator(defender);

        // 确定武器伤害类型和是否为Terra物品
        WeaponInfo weaponInfo = determineWeaponInfo(attacker);

        return new TerraDamageProtocol(
                attacker, defender,
                attackerCalculator, defenderCalculator,
                weaponInfo.damageType
        );
    }

    /**
     * 确定武器信息（伤害类型和是否为Terra物品）
     */
    private WeaponInfo determineWeaponInfo(LivingEntity attacker) {
        EntityEquipment equipment = attacker.getEquipment();
        if (equipment == null) {
            return new WeaponInfo(DamageFromType.OTHER, false);
        }

        ItemStack mainHandItem = equipment.getItemInMainHand();
        TerraBaseItem bit = TerraBukkitAdapter.itemAdapt(mainHandItem);
        if (bit instanceof Item it) {
            return new WeaponInfo(it.getDamageType(), true);
        }

        return new WeaponInfo(DamageFromType.OTHER, false);
    }

    /**
     * 处理前置伤害Buff
     */
    private double processBeforeDamageBuffs(TerraDamageProtocol context, TerraJSEngineManager jsEngine) {
        List<TerraRunnableBuff> beforeBuffs = collectBuffs(
                context.getAttackerCalculator(), BuffActiveCondition.ATTACKER,
                context.getDefenderCalculator(), BuffActiveCondition.DEFENDER
        );
        return executeBuffs(beforeBuffs, context, jsEngine);
    }

    /**
     * 计算基础伤害（含武器白值、攻击冷却修正等）
     */
    private double calculateBaseDamage(TerraDamageProtocol context) {
        double baseDamage = context.getAttackerCalculator().getMeta().get(AttributeType.ATTACK_DAMAGE);
        if (context.getAttacker() instanceof Player player) baseDamage *= 0.15 + player.getAttackCooldown() * 0.85;
        return baseDamage * (1 + context.getAttackerCalculator().getMeta().get(context.getWeaponDamageType()));
    }

    /**
     * 应用暴击和伤害浮动
     */
    private double applyCriticalAndFloatDamage(TerraDamageProtocol context, double currentDamage) {
        // 暴击计算
        if (rand.nextDouble() < context.attackerMeta.get(AttributeType.CRITICAL_STRIKE_CHANCE)) {
            this.critical = true;
            double critDamage = context.attackerMeta.get(AttributeType.CRITICAL_STRIKE_DAMAGE);
            currentDamage *= Math.max(1, critDamage); // 确保至少1倍伤害
        } else {
            this.critical = false;
        }

        // 伤害浮动
        if (damageFloat) {
            currentDamage *= rand.nextDouble(1 - floatRange, 1 + floatRange);
        }

        return currentDamage;
    }

    /**
     * 处理中间阶段伤害Buff
     */
    private double processBetweenDamageBuffs(TerraDamageProtocol context, TerraJSEngineManager jsEngine) {
        List<TerraRunnableBuff> betweenBuffs = collectBuffs(
                context.attackerCalculator, BuffActiveCondition.ATTACKER,
                context.defenderCalculator, BuffActiveCondition.DEFENDER
        );
        return executeBuffs(betweenBuffs, context, jsEngine);
    }

    /**
     * 应用防御方减免（护甲、减伤等）
     */
    private double applyDefenseReductions(TerraDamageProtocol context, double currentDamage) {
        // 护甲前减伤
        currentDamage *= (1 - context.defenderMeta.get(AttributeType.PRE_ARMOR_REDUCTION));

        // 护甲减免
        currentDamage -= context.defenderMeta.get(AttributeType.ARMOR) * worldK;

        // 确保伤害不为负
        currentDamage = Math.max(0, currentDamage);

        // 护甲后减伤
        currentDamage *= (1 - context.defenderMeta.get(AttributeType.AFTER_ARMOR_REDUCTION));

        return currentDamage;
    }

    /**
     * 处理后置伤害Buff
     */
    private double processAfterDamageBuffs(TerraDamageProtocol context, TerraJSEngineManager jsEngine) {
        List<TerraRunnableBuff> afterBuffs = collectBuffs(
                context.attackerCalculator, BuffActiveCondition.ATTACKER,
                context.defenderCalculator, BuffActiveCondition.DEFENDER
        );
        return executeBuffs(afterBuffs, context, jsEngine);
    }

    /**
     * 执行Buff列表，返回处理后的伤害（-1表示继续流程）
     */
    private double executeBuffs(List<TerraRunnableBuff> buffs, DamageAttributes damageAttrs, TerraJSEngineManager jsEngine) {
        for (TerraRunnableBuff buff : buffs) {
            boolean continueProcess = jsEngine.executeFunction(buff.getFileName(), damageAttrs);
            if (!continueProcess) {
                return damageAttrs.getDamage(); // Buff中断流程，返回当前伤害
            }
        }
        return -1; // 继续流程
    }

    /**
     * 为攻击方和防御方激活相关 buff（保持原逻辑）
     */
    protected void activateBuffForAttackerAndDefender(LivingEntity attacker, LivingEntity defender) {
        TerraBuffManager buffManager = TerraCraftBukkit.inst().getBuffManager();
        /* attacker 给 defender 增加 buff */
        for (TerraItem i : EquipmentUtil.getActiveEquipmentItem(attacker)){
            buffManager.activateBuffs(attacker, i.getAttackBuffsForSelf());
            buffManager.activateBuffs(defender, i.getAttackBuffsForOther());
        }
        /* defender 给 attacker 增加 buff */
        for (TerraItem i : EquipmentUtil.getActiveEquipmentItem(defender)){
            buffManager.activateBuffs(defender, i.getDefenseBuffsForSelf());
            buffManager.activateBuffs(attacker, i.getDefenseBuffsForOther());
        }
    }

    /**
     * 武器信息封装类
     */
    protected static class WeaponInfo {
        final DamageFromType damageType;
        final boolean isTerraItem;

        public WeaponInfo(DamageFromType damageType, boolean isTerraItem) {
            this.damageType = damageType;
            this.isTerraItem = isTerraItem;
        }
    }
}
