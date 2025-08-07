package io.github.tanice.terraCraft.core.calculator;

import io.github.tanice.terraCraft.api.attribute.AttributeType;
import io.github.tanice.terraCraft.api.attribute.DamageFromType;
import io.github.tanice.terraCraft.api.attribute.TerraEntityAttributeManager;
import io.github.tanice.terraCraft.api.attribute.calculator.TerraAttributeCalculator;
import io.github.tanice.terraCraft.api.buffs.BuffActiveCondition;
import io.github.tanice.terraCraft.api.buffs.TerraBuffManager;
import io.github.tanice.terraCraft.api.buffs.TerraRunnableBuff;
import io.github.tanice.terraCraft.api.config.TerraConfigManager;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.TerraItem;
import io.github.tanice.terraCraft.api.protocol.TerraDamageProtocol;
import io.github.tanice.terraCraft.api.utils.js.TerraJSEngineManager;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.items.Item;
import io.github.tanice.terraCraft.bukkit.utils.EquipmentUtil;
import io.github.tanice.terraCraft.bukkit.utils.adapter.TerraBukkitAdapter;
import io.github.tanice.terraCraft.bukkit.utils.annotation.NonnullByDefault;
import io.github.tanice.terraCraft.core.skills.SkillDamageMeta;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@NonnullByDefault
public final class DamageCalculator {

    private static final TerraEntityAttributeManager attributeManager = TerraCraftBukkit.inst().getEntityAttributeManager();
    private static final TerraJSEngineManager jsEngine = TerraCraftBukkit.inst().getJSEngineManager();
    private static final TerraConfigManager configManager = TerraCraftBukkit.inst().getConfigManager();
    private static final Random rand = new Random();

    /** 非生物来源的物理伤害 */
    public static TerraDamageProtocol calculate(LivingEntity defender, double oriDamage) {
        return calculate(null, defender, oriDamage);
    }

    /** 物理攻击伤害 */
    private static TerraDamageProtocol calculate(@Nullable LivingEntity attacker, LivingEntity defender, double oriDamage) {
        TerraAttributeCalculator attackerCalculator = attacker == null ? null : attributeManager.getAttributeCalculator(attacker);
        TerraAttributeCalculator defenderCalculator = attributeManager.getAttributeCalculator(defender);

        DamageFromType type = getDamageType(attacker);
        TerraDamageProtocol protocol = new TerraDamageProtocol(attacker, defender, attackerCalculator, defenderCalculator, type, oriDamage);

        /* 前置Buff处理 */
        if (!processBeforeBuffs(protocol, false)) {
            if (protocol.isHit()) activateBuffForAttackerAndDefender(attacker, defender);
            return protocol;
        }

        /* 计算基础伤害（含武器特性、攻击冷却等） */
        calculateBaseDamage(protocol, type);
        /* 处理暴击和伤害浮动 */
        applyCriticalDamage(protocol);
        applyFloatDamage(protocol);

        /* 中间（防御前）Buff处理 */
        if (!processBetweenBuffs(protocol, false)) {
            if (protocol.isHit()) activateBuffForAttackerAndDefender(attacker, defender);
            return protocol;
        }

        /* 防御方减免计算 */
        applyDefenseReductions(protocol);

        /* 防后Buff处理 */
        if (!processAfterBuffs(protocol, false)) {
            if (protocol.isHit()) activateBuffForAttackerAndDefender(attacker, defender);
            return protocol;
        }

        return protocol;
    }

    /** 技能伤害 */
    private static TerraDamageProtocol calculate(LivingEntity attacker, LivingEntity defender, SkillDamageMeta skillMeta) {
        TerraAttributeCalculator attackerCalculator = attributeManager.getAttributeCalculator(attacker);
        TerraAttributeCalculator defenderCalculator = attributeManager.getAttributeCalculator(defender);

        DamageFromType type = getDamageType(attacker);
        TerraDamageProtocol protocol = new TerraDamageProtocol(attacker, defender, attackerCalculator, defenderCalculator, type, 0);
        /* 前置Buff处理 */
        if (!processBeforeBuffs(protocol, true)) {
            if (protocol.isHit()) activateBuffForAttackerAndDefender(attacker, defender);
            return protocol;
        }
        /* 伤害浮动 */
        if(skillMeta.getDamage() < 0) {
            protocol.setFinalDamage(skillMeta.getDamageK() * attackerCalculator.getMeta().get(AttributeType.ATTACK_DAMAGE));
        } else protocol.setFinalDamage(skillMeta.getDamage());
        /* 暴击和伤害浮动 */
        applyCriticalDamage(protocol, skillMeta);
        applyFloatDamage(protocol);
        /* 中间（防御前）Buff处理 */
        if (!processBetweenBuffs(protocol, true)) {
            if (protocol.isHit()) activateBuffForAttackerAndDefender(attacker, defender);
            return protocol;
        }
        /* 防御方减免计算 */
        applyDefenseReductions(protocol, skillMeta);
        /* 防后Buff处理 */
        if (!processAfterBuffs(protocol, false)) {
            if (protocol.isHit()) activateBuffForAttackerAndDefender(attacker, defender);
            return protocol;
        }
        return protocol;
    }

    private static DamageFromType getDamageType(@Nullable LivingEntity attacker) {
        if (attacker == null) return DamageFromType.OTHER;

        EntityEquipment equipment = attacker.getEquipment();
        if (equipment == null) return DamageFromType.OTHER;

        ItemStack mainHandItem = equipment.getItemInMainHand();
        TerraBaseItem bit = TerraBukkitAdapter.itemAdapt(mainHandItem);
        if (bit instanceof Item it) return it.getDamageType();

        return DamageFromType.OTHER;
    }

    private static void calculateBaseDamage(TerraDamageProtocol protocol, DamageFromType type) {
        TerraAttributeCalculator ac = protocol.getAttackerCalculator();
        double oriDamage = protocol.getFinalDamage();
        if (type != DamageFromType.OTHER && ac != null) {
            oriDamage =  ac.getMeta().get(AttributeType.ATTACK_DAMAGE);
            if (protocol.getAttacker() instanceof Player player) oriDamage *= 0.15 + player.getAttackCooldown() * 0.85;
            oriDamage *= (1 + ac.getMeta().get(protocol.getWeaponDamageType()));
        }
        protocol.setFinalDamage(oriDamage);
    }

    private static void applyCriticalDamage(TerraDamageProtocol protocol) {
        TerraAttributeCalculator ac = protocol.getAttackerCalculator();
        double damage = protocol.getFinalDamage();
        // 暴击计算
        if (ac != null && rand.nextDouble() < ac.getMeta().get(AttributeType.CRITICAL_STRIKE_CHANCE)) {
            double critDamage = ac.getMeta().get(AttributeType.CRITICAL_STRIKE_DAMAGE);
            damage *= Math.max(1, critDamage);
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
        if (configManager.isDamageFloatEnabled()) {
            double r = configManager.getDamageFloatRange();
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
        damage *= (1 - protocol.getDefenderCalculator().getMeta().get(AttributeType.PRE_ARMOR_REDUCTION));
        damage -= protocol.getDefenderCalculator().getMeta().get(AttributeType.ARMOR) * configManager.getWorldK();
        damage = Math.max(0, damage);
        damage *= (1 - protocol.getDefenderCalculator().getMeta().get(AttributeType.AFTER_ARMOR_REDUCTION));
        protocol.setFinalDamage(damage);
    }

    private static void applyDefenseReductions(TerraDamageProtocol protocol, SkillDamageMeta skillMeta) {
        double damage = protocol.getFinalDamage();
        damage *= (1 - protocol.getDefenderCalculator().getMeta().get(AttributeType.PRE_ARMOR_REDUCTION));
        if (!skillMeta.isIgnoreArmor())
            damage -= protocol.getDefenderCalculator().getMeta().get(AttributeType.ARMOR) * configManager.getWorldK();
        damage = Math.max(0, damage);
        damage *= (1 - protocol.getDefenderCalculator().getMeta().get(AttributeType.AFTER_ARMOR_REDUCTION));
        protocol.setFinalDamage(damage);
    }

    private static void activateBuffForAttackerAndDefender(@Nullable LivingEntity attacker, LivingEntity defender) {
        TerraBuffManager buffManager = TerraCraftBukkit.inst().getBuffManager();
        /* attacker 给 defender 增加 buff */
        if (attacker != null) {
            for (TerraItem i : EquipmentUtil.getActiveEquipmentItem(attacker)){
                buffManager.activateBuffs(attacker, i.getAttackBuffsForSelf());
                buffManager.activateBuffs(defender, i.getAttackBuffsForOther());
            }
        }
        /* defender 给 attacker 增加 buff */
        for (TerraItem i : EquipmentUtil.getActiveEquipmentItem(defender)){
            buffManager.activateBuffs(defender, i.getDefenseBuffsForSelf());
            if (attacker != null) buffManager.activateBuffs(attacker, i.getDefenseBuffsForOther());
        }
    }
}
