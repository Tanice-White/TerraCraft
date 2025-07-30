package io.github.tanice.terraCraft.core.skills.meta;

import io.github.tanice.terraCraft.api.attribute.DamageFromType;

/**
 * 技能元数据类，存储技能的基本信息
 */
public class SkillMetaData {
    private final String skillId;
    private final String skillName;
    private final double baseDamage;
    private final DamageFromType damageType;
    private final boolean canCrit;
    private final double critChance;
    private final double critMultiplier;
    
    /**
     * 创建技能元数据
     *
     * @param skillId 技能ID
     * @param skillName 技能名称
     * @param baseDamage 基础伤害
     * @param damageType 伤害类型
     * @param canCrit 是否可以暴击
     * @param critChance 暴击率
     * @param critMultiplier 暴击倍率
     */
    public SkillMetaData(String skillId, String skillName, double baseDamage, DamageFromType damageType, boolean canCrit, double critChance, double critMultiplier) {
        this.skillId = skillId;
        this.skillName = skillName;
        this.baseDamage = baseDamage;
        this.damageType = damageType;
        this.canCrit = canCrit;
        this.critChance = critChance;
        this.critMultiplier = critMultiplier;
    }
    
    /**
     * 创建简单的技能元数据
     * @param skillId 技能ID
     * @param baseDamage 基础伤害
     * @param damageType 伤害类型
     */
    public SkillMetaData(String skillId, double baseDamage, DamageFromType damageType) {
        this(skillId, skillId, baseDamage, damageType, true, 0.1, 1.5);
    }
    
    public String getSkillId() {
        return skillId;
    }
    
    public String getSkillName() {
        return skillName;
    }
    
    public double getBaseDamage() {
        return baseDamage;
    }
    
    public DamageFromType getDamageFromType() {
        return damageType;
    }
    
    public boolean canCrit() {
        return canCrit;
    }
    
    public double getCritChance() {
        return critChance;
    }
    
    public double getCritMultiplier() {
        return critMultiplier;
    }
}