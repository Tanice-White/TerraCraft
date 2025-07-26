package io.github.tanice.terraCraft.bukkit.events.entity;

import io.github.tanice.terraCraft.bukkit.utils.annotation.NonnullByDefault;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Random;

/**
 * 插件伤害事件
 */
@NonnullByDefault
public class TerraEntityDamageEvent extends Event implements Cancellable {
    protected static final HandlerList handlers = new HandlerList();
    protected boolean cancelled;

    protected final Random rand = new Random();
    /** 防御方 */
    protected LivingEntity defender;
    /** 是否为原版跳劈(暴击) */
    protected boolean critical;
    /** 初始伤害 */
    protected double damage;

    protected boolean damageFloat;
    protected double floatRange;
    protected double worldK;

    public TerraEntityDamageEvent(LivingEntity defender, double oriDamage) {
        this.defender = defender;
        this.damage = oriDamage;
        critical = false;
        this.initFormConfig();
    }

    public static HandlerList getHandlerList() {return handlers;}

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }

    public double getFinalDamage() {
        LivingEntityCombatPowerCalculator defenderCalculator = (LivingEntityCombatPowerCalculator) TwItemManager.getEntityAttributeManager().getCalculator(defender);
        EnumMap<AttributeType, Double> defenderAttributeTypeModifiers = defenderCalculator.getAttributeTypeModifiers();

        double finalDamage = damage;

        DamageAttributes damageAttributes = new DamageAttributes(defender, 0, defenderAttributeTypeModifiers);
        /* BEFORE_DAMAGE 事件计算 */
        for (BuffPDC pdc : defenderCalculator.getOrderedBeforeList(BuffActiveCondition.DEFENDER)) {
            Object answer = pdc.execute(damageAttributes);
            /* 可能被更改 */
            finalDamage = damageAttributes.getDamage();
            if (answer.equals(false)) return finalDamage;
        }
        damageAttributes.setDamage(finalDamage);

        /* 中间属性生效 */
        for (BuffPDC pdc : defenderCalculator.getOrderedBetweenList(BuffActiveCondition.DEFENDER)) {
            Object answer = pdc.execute(damageAttributes);
            /* 可能被更改 */
            finalDamage = damageAttributes.getDamage();
            if (answer.equals(false)) return finalDamage;
        }

        /* 最终伤害计算 */
        finalDamage *= (1 - defenderAttributeTypeModifiers.get(AttributeType.PRE_ARMOR_REDUCTION));
        finalDamage -= defenderAttributeTypeModifiers.get(AttributeType.ARMOR) * worldK;
        /* 数值修正 */
        finalDamage = Math.max(0, finalDamage);
        finalDamage *= (1 - defenderAttributeTypeModifiers.get(AttributeType.AFTER_ARMOR_REDUCTION));

        /* AFTER_DAMAGE 事件计算 */
        damageAttributes.setDamage(finalDamage);

        for (BuffPDC pdc : defenderCalculator.getOrderedAfterList(BuffActiveCondition.DEFENDER)) {
            Object answer = pdc.execute(damageAttributes);
            /* 可能被更改 */
            finalDamage = damageAttributes.getDamage();
            if (answer.equals(false)) return finalDamage;
        }

        /* defender 给 自己 增加 buff */
        boolean f = false;
        for (Item i : EquipmentUtil.getActiveEquipmentItem(defender)){
            f |= TwItemManager.getBuffManager().activateBuffs(defender, i.getDefenseBuffs().getFirst());
        }

        if (f) Bukkit.getPluginManager().callEvent(new TerraAttributeUpdateEvent(defender));
        return finalDamage;
    }

    /**
     * 加载全局配置中的信息
     */
    protected void initFormConfig() {
        /* 伤害计算配置 */
        worldK = Config.worldK;
        damageFloat = Config.damageFloat;
        floatRange = Config.damageFloatRange;
    }
}
