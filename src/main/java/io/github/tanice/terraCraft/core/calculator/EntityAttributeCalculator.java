package io.github.tanice.terraCraft.core.calculator;

import io.github.tanice.terraCraft.api.attribute.AttributeActiveSection;
import io.github.tanice.terraCraft.api.attribute.AttributeType;
import io.github.tanice.terraCraft.api.attribute.DamageFromType;
import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import io.github.tanice.terraCraft.api.attribute.calculator.TerraAttributeCalculator;
import io.github.tanice.terraCraft.api.buff.BuffActiveCondition;
import io.github.tanice.terraCraft.api.buff.TerraBaseBuff;
import io.github.tanice.terraCraft.api.buff.TerraRunnableBuff;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.core.attribute.CalculableMeta;
import io.github.tanice.terraCraft.core.buff.impl.AttributeBuff;
import io.github.tanice.terraCraft.core.config.ConfigManager;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import io.github.tanice.terraCraft.bukkit.util.EquipmentUtil;
import io.github.tanice.terraCraft.core.registry.Registry;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;

import java.util.*;
import java.util.stream.Collectors;


public class EntityAttributeCalculator implements TerraAttributeCalculator {
    /** 是否使用玩家减伤平衡算法 */
    protected boolean useDamageReductionBalance;

    protected TerraCalculableMeta meta;
    protected List<TerraRunnableBuff> beforeList;
    protected List<TerraRunnableBuff> betweenList;
    protected List<TerraRunnableBuff> afterList;
    /**
     * 中间值 用于将属性从 AttributeActiveSection 转化为 AttributeType 的 Map
     */
    protected EnumMap<AttributeActiveSection, TerraCalculableMeta> transformTmp;

    public EntityAttributeCalculator(LivingEntity entity) {
        this.useDamageReductionBalance = ConfigManager.useDamageReductionBalanceForPlayer();
        this.transformTmp = new EnumMap<>(AttributeActiveSection.class);
        this.beforeList = new ArrayList<>();
        this.betweenList = new ArrayList<>();
        this.afterList = new ArrayList<>();
        this.meta = new CalculableMeta(AttributeActiveSection.BASE);

        this.initBuffListsAndTransformTmp(entity);
        this.initDamageTypeModifiers();
        this.initAttributeTypeModifiers();
    }

    @Override
    public List<TerraRunnableBuff> getOrderedBeforeList(BuffActiveCondition condition) {
        return beforeList.stream()
                .filter(buff -> buff.enabled() && buff.isActiveUnder(condition))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<TerraRunnableBuff> getOrderedBetweenList(BuffActiveCondition condition) {
        return betweenList.stream()
                .filter(buff -> buff.enabled() && buff.isActiveUnder(condition))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<TerraRunnableBuff> getOrderedAfterList(BuffActiveCondition condition) {
        return afterList.stream()
                .filter(buff -> buff.enabled() && buff.isActiveUnder(condition))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public TerraCalculableMeta getMeta() {
        return this.meta.clone();
    }

    /**
     * 获取 BuffList和中间 Map
     */
    private void initBuffListsAndTransformTmp(LivingEntity entity) {
        List<TerraCalculableMeta> metas = EquipmentUtil.getEntityActiveMeta(entity);
        metas.addAll(EquipmentUtil.getEffectiveAccessoryMeta(entity));
        /* 原版药水效果计算 */
        TerraCalculableMeta potionMeta;
        TerraCalculableMeta tmp;
        for (PotionEffect effect : entity.getActivePotionEffects()) {
            potionMeta = Registry.ORI_POTION.get(effect.getType().getKey().getKey().toLowerCase());
            if (potionMeta != null) {
                tmp = potionMeta.clone();
                metas.add(tmp.selfMultiply(effect.getAmplifier() + 1));
            }
        }
        /* buff计算 */
        for (TerraBaseBuff baseBuff : TerraCraftBukkit.inst().getBuffManager().getEntityActiveBuffs(entity)) {
            if (baseBuff instanceof AttributeBuff attributeBuff) metas.add(attributeBuff.getMeta());
            else {
                if (baseBuff.getAttributeActiveSection() == AttributeActiveSection.BEFORE_DAMAGE) beforeList.add((TerraRunnableBuff) baseBuff);
                else if (baseBuff.getAttributeActiveSection() == AttributeActiveSection.BETWEEN_DAMAGE_AND_DEFENCE) betweenList.add((TerraRunnableBuff) baseBuff);
                else if (baseBuff.getAttributeActiveSection() == AttributeActiveSection.AFTER_DAMAGE) afterList.add((TerraRunnableBuff) baseBuff);
            }
        }

        if (ConfigManager.isDebug()) {
            TerraCraftLogger.debug(TerraCraftLogger.DebugLevel.CALCULATOR, metas.size() + " metas in " + entity.getName());
        }

        AttributeActiveSection aas;
        for (TerraCalculableMeta m : metas) {
            aas = m.getActiveSection();
            if (aas != AttributeActiveSection.ERROR) {
                tmp = transformTmp.getOrDefault(aas, new CalculableMeta(aas));
                tmp.add(m, 1);
                transformTmp.put(aas, tmp);
            }
        }
    }

    private void initDamageTypeModifiers() {
        TerraCalculableMeta calculableMeta;
        double[] dmgMods = meta.getDamageTypeModifierArray();

        for (AttributeActiveSection acs : transformTmp.keySet()) {
            if (!acs.canCalculateInMeta()) continue;

            calculableMeta = transformTmp.get(acs);
            /* 属性加成相加 */
            for (DamageFromType dt : DamageFromType.values()) {
                dmgMods[dt.ordinal()] += calculableMeta.get(dt);
            }
        }
    }

    private void initAttributeTypeModifiers() {
        TerraCalculableMeta calculableMeta;
        double result = 0D;
        double[] attrMods = meta.getAttributeModifierArray();

        for (AttributeType type : AttributeType.values()) {
            calculableMeta = transformTmp.get(AttributeActiveSection.BASE);
            if (calculableMeta != null) result = calculableMeta.get(type);
            /* 计算顺序：BASE * ADD * MULTIPLY * FIX */
            if (result != 0D){
                calculableMeta = transformTmp.get(AttributeActiveSection.ADD);
                if (calculableMeta != null) result *= (1 + calculableMeta.get(type));

                calculableMeta = transformTmp.get(AttributeActiveSection.MULTIPLY);
                if (calculableMeta != null) result *= (1 + calculableMeta.get(type));

                calculableMeta = transformTmp.get(AttributeActiveSection.FIX);
                if (calculableMeta != null) result *= (1 + calculableMeta.get(type));
            }
            attrMods[type.ordinal()] = result;
        }
        attrMods[AttributeType.PRE_ARMOR_REDUCTION.ordinal()] = drBalance(attrMods[AttributeType.PRE_ARMOR_REDUCTION.ordinal()]);
        attrMods[AttributeType.AFTER_ARMOR_REDUCTION.ordinal()] = drBalance(attrMods[AttributeType.AFTER_ARMOR_REDUCTION.ordinal()]);
    }

    /**
     * 减伤平衡算法
     * @param oriDr 理论伤害减免比例
     * @return 平衡后的伤害减免比例
     */
    private double drBalance(double oriDr) {
        if (oriDr == 0D) return 0D;
        if (useDamageReductionBalance) return oriDr / (1 + oriDr);
        return oriDr;
    }
}
