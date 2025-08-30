package io.github.tanice.terraCraft.core.calculator;

import io.github.tanice.terraCraft.api.attribute.AttributeType;
import io.github.tanice.terraCraft.api.attribute.DamageFromType;
import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import io.github.tanice.terraCraft.api.attribute.TerraEntityAttributeManager;
import io.github.tanice.terraCraft.api.attribute.calculator.TerraAttributeCalculator;
import io.github.tanice.terraCraft.api.buff.BuffActiveCondition;
import io.github.tanice.terraCraft.api.buff.TerraRunnableBuff;
import io.github.tanice.terraCraft.api.protocol.TerraDamageProtocol;
import io.github.tanice.terraCraft.api.js.TerraJSEngineManager;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.item.component.DamageTypeComponent;
import io.github.tanice.terraCraft.bukkit.util.annotation.NonnullByDefault;
import io.github.tanice.terraCraft.core.config.ConfigManager;
import io.github.tanice.terraCraft.core.util.logger.TerraCraftLogger;
import io.github.tanice.terraCraft.core.skill.SkillDamageMeta;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@NonnullByDefault
public final class DamageCalculator {

    private static final TerraEntityAttributeManager attributeManager = TerraCraftBukkit.inst().getEntityAttributeManager();
    private static final TerraJSEngineManager jsEngine = TerraCraftBukkit.inst().getJSEngineManager();
    private static final Random rand = new Random();

    /** 非生物来源的物理伤害 */
    public static TerraDamageProtocol calculate(LivingEntity defender, double oriDamage, boolean isOriCritical, boolean isMelee) {
        return calculate(null, defender, oriDamage, isOriCritical, isMelee);
    }

    /** 物理攻击伤害 */
    public static TerraDamageProtocol calculate(@Nullable LivingEntity attacker, LivingEntity defender, double oriDamage, boolean isOriCritical, boolean isMelee) {
        TerraAttributeCalculator attackerCalculator = attacker == null ? null : attributeManager.getAttributeCalculator(attacker);
        TerraAttributeCalculator defenderCalculator = attributeManager.getAttributeCalculator(defender);

        DamageFromType type = getDamageType(attacker);
        TerraDamageProtocol protocol = new TerraDamageProtocol(attacker, defender, attackerCalculator, defenderCalculator, type, oriDamage, isOriCritical, isMelee);
        /* 前置Buff处理 */
        if (!processBeforeBuffs(protocol, false)) return protocol;

        if (ConfigManager.isDebug())
            TerraCraftLogger.debug(TerraCraftLogger.DebugLevel.CALCULATOR, "1-AfterCreate: " + protocol.getFinalDamage());

        /* 计算基础伤害（含武器特性、攻击冷却等） */
        calculateBaseDamage(protocol, isOriCritical);
        if (ConfigManager.isDebug())
            TerraCraftLogger.debug(TerraCraftLogger.DebugLevel.CALCULATOR, "2-AfterBaseCalculation: " + protocol.getFinalDamage());

        applyCriticalDamage(protocol);
        if (ConfigManager.isDebug())
            TerraCraftLogger.debug(TerraCraftLogger.DebugLevel.CALCULATOR, "3-AfterCriticalJudge: " + protocol.getFinalDamage());

        applyFloatDamage(protocol);
        if (ConfigManager.isDebug())
            TerraCraftLogger.debug(TerraCraftLogger.DebugLevel.CALCULATOR, "4-AfterDamageFloat: " + protocol.getFinalDamage());

        /* 中间（防御前）Buff处理 */
        if (!processBetweenBuffs(protocol, false)) return protocol;
        /* 防御方减免计算 */
        applyDefenseReductions(protocol);
        if (ConfigManager.isDebug())
            TerraCraftLogger.debug(TerraCraftLogger.DebugLevel.CALCULATOR, "5-AfterDefenseReduction: " + protocol.getFinalDamage());

        /* 防后Buff处理 */
        processAfterBuffs(protocol, false);
        return protocol;
    }

    /** 技能伤害 */
    public static TerraDamageProtocol calculate(LivingEntity attacker, LivingEntity defender, SkillDamageMeta skillMeta) {
        TerraAttributeCalculator attackerCalculator = attributeManager.getAttributeCalculator(attacker);
        TerraAttributeCalculator defenderCalculator = attributeManager.getAttributeCalculator(defender);

        DamageFromType type = getDamageType(attacker);
        TerraDamageProtocol protocol = new TerraDamageProtocol(attacker, defender, attackerCalculator, defenderCalculator, type, 0, false, false);
        /* 前置Buff处理 */
        if (!processBeforeBuffs(protocol, true)) return protocol;
        /* 伤害浮动 */
        if(skillMeta.getDamage() < 0) {
            protocol.setFinalDamage(skillMeta.getDamageK() * attackerCalculator.getMeta().get(AttributeType.ATTACK_DAMAGE));
        } else protocol.setFinalDamage(skillMeta.getDamage());
        /* 暴击和伤害浮动 */
        applyCriticalDamage(protocol, skillMeta);
        applyFloatDamage(protocol);
        /* 中间（防御前）Buff处理 */
        if (!processBetweenBuffs(protocol, true)) return protocol;
        /* 防御方减免计算 */
        applyDefenseReductions(protocol, skillMeta);
        /* 防后Buff处理 */
        processAfterBuffs(protocol, true);
        return protocol;
    }

    private static DamageFromType getDamageType(@Nullable LivingEntity attacker) {
        if (attacker == null) return DamageFromType.OTHER;

        EntityEquipment equipment = attacker.getEquipment();
        if (equipment == null || equipment.getItemInMainHand().isEmpty()) return DamageFromType.OTHER;
        DamageTypeComponent damageTypeComponent = DamageTypeComponent.from(equipment.getItemInMainHand());
        return damageTypeComponent == null ? DamageFromType.OTHER : damageTypeComponent.getType();
    }

    private static void calculateBaseDamage(TerraDamageProtocol protocol, boolean isOriCritical) {
        TerraAttributeCalculator ac = protocol.getAttackerCalculator();
        double damage = protocol.getFinalDamage();
        if (ac != null) {
            damage +=  ac.getMeta().get(AttributeType.ATTACK_DAMAGE);
            /* 玩家近战 */
            if (protocol.isMelee() && protocol.getAttacker() instanceof Player player) damage *= 0.15 + player.getAttackCooldown() * 0.85;
            damage *= (1 + ac.getMeta().get(protocol.getWeaponDamageType()));
        }
        if (isOriCritical) damage *= ConfigManager.getOriginalCriticalStrikeAddition() + 1;
        protocol.setFinalDamage(damage);
    }

    private static void applyCriticalDamage(TerraDamageProtocol protocol) {
        TerraAttributeCalculator ac = protocol.getAttackerCalculator();
        double damage = protocol.getFinalDamage();
        // 暴击计算
        if (ac != null && rand.nextDouble() < ac.getMeta().get(AttributeType.CRITICAL_STRIKE_CHANCE)) {
            damage *= Math.max(1, ac.getMeta().get(AttributeType.CRITICAL_STRIKE_DAMAGE));
        }
        protocol.setFinalDamage(damage);
    }

    private static void applyCriticalDamage(TerraDamageProtocol protocol, SkillDamageMeta skillMeta) {
        if (!skillMeta.canCritical()) return;
        TerraAttributeCalculator ac = protocol.getAttackerCalculator();
        if (ac == null) return;

        double v = ac.getMeta().get(AttributeType.CRITICAL_STRIKE_CHANCE);
        // 暴击计算
        if (skillMeta.getCriticalChance() < 0) v *= skillMeta.getCriticalK();
        else v = skillMeta.getCriticalChance();

        if (rand.nextDouble() < v) {
            double damage = protocol.getFinalDamage();
            damage *= Math.max(1, ac.getMeta().get(AttributeType.CRITICAL_STRIKE_DAMAGE));
            protocol.setFinalDamage(damage);
        }
    }

    private static void applyFloatDamage(TerraDamageProtocol protocol) {
        double damage = protocol.getFinalDamage();
        if (ConfigManager.isDamageFloatEnabled()) {
            double r = ConfigManager.getDamageFloatRange();
            damage *= rand.nextDouble(1 - r, 1 + r);
        }
        protocol.setFinalDamage(damage);
    }

    private static boolean processBeforeBuffs(TerraDamageProtocol protocol, boolean isSkill) {
        BuffActiveCondition a = isSkill ? BuffActiveCondition.ATTACKER_SKILL : BuffActiveCondition.ATTACKER;
        BuffActiveCondition b = isSkill ? BuffActiveCondition.DEFENDER_SKILL : BuffActiveCondition.DEFENDER;

        List<TerraRunnableBuff> bd = protocol.getDefenderCalculator().getOrderedBeforeList(b);
        if (protocol.getAttackerCalculator() != null) bd.addAll(protocol.getAttackerCalculator().getOrderedBeforeList(a));
        Collections.sort(bd);
        boolean answer;

        for (TerraRunnableBuff buff : bd) {
            answer = jsEngine.executeFunction(buff.getFileName(), protocol);
            /* 返回false表示后续不执行 */
            if (!answer) return false;
        }
        /* 后续执行 */
        return true;
    }

    private static boolean processBetweenBuffs(TerraDamageProtocol protocol, boolean isSkill) {
        BuffActiveCondition a = isSkill ? BuffActiveCondition.ATTACKER_SKILL : BuffActiveCondition.ATTACKER;
        BuffActiveCondition b = isSkill ? BuffActiveCondition.DEFENDER_SKILL : BuffActiveCondition.DEFENDER;

        List<TerraRunnableBuff> bd = protocol.getDefenderCalculator().getOrderedBetweenList(b);
        if (protocol.getAttackerCalculator() != null) bd.addAll(protocol.getAttackerCalculator().getOrderedBetweenList(a));
        Collections.sort(bd);
        boolean answer;
        for (TerraRunnableBuff buff : bd) {
            answer = jsEngine.executeFunction(buff.getFileName(), protocol);
            if (!answer) return false;
        }
        return true;
    }

    private static boolean processAfterBuffs(TerraDamageProtocol protocol, boolean isSkill) {
        BuffActiveCondition a = isSkill ? BuffActiveCondition.ATTACKER_SKILL : BuffActiveCondition.ATTACKER;
        BuffActiveCondition b = isSkill ? BuffActiveCondition.DEFENDER_SKILL : BuffActiveCondition.DEFENDER;

        List<TerraRunnableBuff> bd = protocol.getDefenderCalculator().getOrderedAfterList(b);
        if (protocol.getAttackerCalculator() != null) bd.addAll(protocol.getAttackerCalculator().getOrderedAfterList(a));
        Collections.sort(bd);
        boolean answer;
        for (TerraRunnableBuff buff : bd) {
            answer = jsEngine.executeFunction(buff.getFileName(), protocol);
            if (!answer) return false;
        }
        return true;
    }

    private static void applyDefenseReductions(TerraDamageProtocol protocol) {
        double damage = protocol.getFinalDamage();
        TerraCalculableMeta dMeta = protocol.getDefenderCalculator().getMeta();

        damage *= 1 - Math.min(1, dMeta.get(AttributeType.PRE_ARMOR_REDUCTION));
        damage -= dMeta.get(AttributeType.ARMOR) * ConfigManager.getWorldK();
        damage = Math.max(0, damage);
        damage *= 1 - Math.min(1, dMeta.get(AttributeType.AFTER_ARMOR_REDUCTION));
        protocol.setFinalDamage(damage);
    }

    private static void applyDefenseReductions(TerraDamageProtocol protocol, SkillDamageMeta skillMeta) {
        double damage = protocol.getFinalDamage();
        TerraCalculableMeta dMeta = protocol.getDefenderCalculator().getMeta();

        damage *= (1 - dMeta.get(AttributeType.PRE_ARMOR_REDUCTION));
        if (!skillMeta.isIgnoreArmor())
            damage -= dMeta.get(AttributeType.ARMOR) * ConfigManager.getWorldK();
        damage = Math.max(0, damage);
        damage *= (1 - dMeta.get(AttributeType.AFTER_ARMOR_REDUCTION));
        protocol.setFinalDamage(damage);
    }
}
