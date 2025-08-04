package io.github.tanice.terraCraft.core.calculator;

import io.github.tanice.terraCraft.api.attribute.AttributeType;
import io.github.tanice.terraCraft.api.attribute.DamageFromType;
import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
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

    /** 非生物来源的伤害 */
    public static double calculate(LivingEntity defender, double oriDamage) {
    }

    /** 生物来源的伤害 */
    public static double calculate(LivingEntity attacker, LivingEntity defender, @Nullable ItemStack weapon, @Nullable ItemStack projectile, double oriDamage) {
    }

    /** 生物来源的技能伤害 */
    public static double calculate(LivingEntity attacker, LivingEntity defender, TerraSkillMeta skillMeta) {

    }

    private static double calculate(LivingEntity attacker, LivingEntity defender, TerraSkillMeta skillMeta, double oriDamage) {

    }

    private static double calculate(LivingEntity attacker, LivingEntity defender, double oriDamage) {
        TerraAttributeCalculator attackerCalculator = attributeManager.getAttributeCalculator(attacker);
        TerraAttributeCalculator defenderCalculator = attributeManager.getAttributeCalculator(defender);

        DamageFromType type = getDamageType(attacker);
        TerraDamageProtocol protocol = new TerraDamageProtocol(attacker, defender, attackerCalculator, defenderCalculator, type, oriDamage);

        /* 前置Buff处理 */
        if (!processBeforeBuffs(protocol)) {
            if (protocol.isHit()) activateBuffForAttackerAndDefender(attacker, defender);
            return protocol.getFinalDamage();
        }

        /* 计算基础伤害（含武器特性、攻击冷却等） */
        double damage = protocol.getFinalDamage();
        damage = calculateBaseDamage(protocol, type, damage);
        /* 处理暴击和伤害浮动 */
        damage = applyCriticalAndFloatDamage(protocol, damage);
        protocol.setFinalDamage(damage);

        /* 中间（防御前）Buff处理 */
        if (!processBetweenBuffs(protocol)) {
            if (protocol.isHit()) activateBuffForAttackerAndDefender(attacker, defender);
            return protocol.getFinalDamage();
        }

        /* 防御方减免计算 */
        damage = applyDefenseReductions(protocol, damage);
        protocol.setFinalDamage(damage);

        /* 防后Buff处理 */
        if (!processAfterBuffs(protocol)) {
            if (protocol.isHit()) activateBuffForAttackerAndDefender(attacker, defender);
            return protocol.getFinalDamage();
        }

        return protocol.getFinalDamage();
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

    private static double calculateBaseDamage(TerraDamageProtocol protocol, DamageFromType type, double oriDamage) {
        double v = oriDamage;
        TerraAttributeCalculator ac = protocol.getAttackerCalculator();
        if (type != DamageFromType.OTHER && ac != null) {
            v =  ac.getMeta().get(AttributeType.ATTACK_DAMAGE);
            if (protocol.getAttacker() instanceof Player player) v *= 0.15 + player.getAttackCooldown() * 0.85;
            v *= (1 + ac.getMeta().get(protocol.getWeaponDamageType()));
        }
        return v;
    }

    private static double applyCriticalAndFloatDamage(TerraDamageProtocol protocol, double damage) {
        TerraAttributeCalculator ac = protocol.getAttackerCalculator();
        // 暴击计算
        if (ac != null && rand.nextDouble() < ac.getMeta().get(AttributeType.CRITICAL_STRIKE_CHANCE)) {
            double critDamage = ac.getMeta().get(AttributeType.CRITICAL_STRIKE_DAMAGE);
            damage *= Math.max(1, critDamage);
        }

        if (configManager.isDamageFloatEnabled()) {
            double r = configManager.getDamageFloatRange();
            damage *= rand.nextDouble(1 - r, 1 + r);
        }
        return damage;
    }

    private static boolean processBeforeBuffs(TerraDamageProtocol context) {
        List<TerraRunnableBuff> bd = context.getDefenderCalculator().getOrderedBeforeList(BuffActiveCondition.DEFENDER);
        if (context.getAttackerCalculator() != null) bd.addAll(context.getAttackerCalculator().getOrderedBeforeList(BuffActiveCondition.ATTACKER));
        Collections.sort(bd);
        boolean answer;

        for (TerraRunnableBuff buff : bd) {
            answer = jsEngine.executeFunction(buff.getFileName(), context);
            /* 返回false表示后续不执行 */
            if (!answer) return false;
        }
        /* 后续执行 */
        return true;
    }

    private static boolean processBetweenBuffs(TerraDamageProtocol context) {
        List<TerraRunnableBuff> bd = context.getDefenderCalculator().getOrderedBetweenList(BuffActiveCondition.DEFENDER);
        if (context.getAttackerCalculator() != null) bd.addAll(context.getAttackerCalculator().getOrderedBetweenList(BuffActiveCondition.ATTACKER));
        Collections.sort(bd);
        boolean answer;
        for (TerraRunnableBuff buff : bd) {
            answer = jsEngine.executeFunction(buff.getFileName(), context);
            if (!answer) return false;
        }
        return true;
    }

    private static boolean processAfterBuffs(TerraDamageProtocol context) {
        List<TerraRunnableBuff> bd = context.getDefenderCalculator().getOrderedAfterList(BuffActiveCondition.DEFENDER);
        if (context.getAttackerCalculator() != null) bd.addAll(context.getAttackerCalculator().getOrderedAfterList(BuffActiveCondition.ATTACKER));
        Collections.sort(bd);
        boolean answer;
        for (TerraRunnableBuff buff : bd) {
            answer = jsEngine.executeFunction(buff.getFileName(), context);
            if (!answer) return false;
        }
        return true;
    }

    private static double applyDefenseReductions(TerraDamageProtocol protocol, double damage) {
        damage *= (1 - protocol.getDefenderCalculator().getMeta().get(AttributeType.PRE_ARMOR_REDUCTION));
        damage -= protocol.getDefenderCalculator().getMeta().get(AttributeType.ARMOR) * configManager.getWorldK();
        damage = Math.max(0, damage);
        damage *= (1 - protocol.getDefenderCalculator().getMeta().get(AttributeType.AFTER_ARMOR_REDUCTION));
        return damage;
    }

    private static void activateBuffForAttackerAndDefender(LivingEntity attacker, LivingEntity defender) {
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
}
