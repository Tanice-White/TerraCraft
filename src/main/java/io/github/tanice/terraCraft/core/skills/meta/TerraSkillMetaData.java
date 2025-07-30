package io.github.tanice.terraCraft.core.skills.meta;

import io.github.tanice.terraCraft.api.attribute.DamageFromType;

/**
 * Terra技能元数据，扩展基础技能元数据
 * 提供别名以保持向后兼容
 */
public class TerraSkillMetaData extends SkillMetaData {
    
    private final double cooldown;
    private final double manaCost;
    private final String description;
    
    /**
     * 创建Terra技能元数据
     * @param skillId 技能ID
     * @param skillName 技能名称
     * @param baseDamage 基础伤害
     * @param damageType 伤害类型
     * @param canCrit 是否可以暴击
     * @param critChance 暴击率
     * @param critMultiplier 暴击倍率
     * @param cooldown 冷却时间（秒）
     * @param manaCost 魔法消耗
     * @param description 技能描述
     */
    public TerraSkillMetaData(String skillId, String skillName, double baseDamage, DamageFromType damageType, boolean canCrit, double critChance, double critMultiplier, double cooldown, double manaCost, String description) {
        super(skillId, skillName, baseDamage, damageType, canCrit, critChance, critMultiplier);
        this.cooldown = cooldown;
        this.manaCost = manaCost;
        this.description = description;
    }
    
    /**
     * 创建简单的Terra技能元数据
     * @param skillId 技能ID
     * @param baseDamage 基础伤害
     * @param damageType 伤害类型
     */
    public TerraSkillMetaData(String skillId, double baseDamage, DamageFromType damageType) {
        this(skillId, skillId, baseDamage, damageType, true, 0.1, 1.5, 0, 0, "");
    }
    
    public double getCooldown() {
        return cooldown;
    }
    
    public double getManaCost() {
        return manaCost;
    }
    
    public String getDescription() {
        return description;
    }
}