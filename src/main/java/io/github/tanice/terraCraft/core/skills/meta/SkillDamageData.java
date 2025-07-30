package io.github.tanice.terraCraft.core.skills.meta;

import io.github.tanice.terraCraft.api.attribute.AttributeType;
import io.github.tanice.terraCraft.api.attribute.DamageFromType;
import org.bukkit.entity.LivingEntity;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * 技能伤害数据，包含技能造成伤害所需的所有信息
 */
public class SkillDamageData {
    
    private final String skillId;
    private final double baseDamage;
    private final DamageFromType damageFromType;
    private final boolean isCritical;
    private final Map<String, Object> metadata;
    private final EnumMap<AttributeType, Double> skillModifiers;
    
    /**
     * 创建技能伤害数据
     * @param skillId 技能ID
     * @param baseDamage 基础伤害
     * @param damageFromType 伤害类型
     * @param isCritical 是否暴击
     * @param skillModifiers 技能属性修饰符
     */
    public SkillDamageData(String skillId, double baseDamage, DamageFromType damageFromType,
                          boolean isCritical, EnumMap<AttributeType, Double> skillModifiers) {
        this.skillId = skillId;
        this.baseDamage = baseDamage;
        this.damageFromType = damageFromType;
        this.isCritical = isCritical;
        this.metadata = new HashMap<>();
        this.skillModifiers = skillModifiers != null ? skillModifiers : new EnumMap<>(AttributeType.class);
    }
    
    /**
     * 创建简单的技能伤害数据
     * @param skillId 技能ID
     * @param baseDamage 基础伤害
     * @param DamageFromType 伤害类型
     */
    public SkillDamageData(String skillId, double baseDamage, DamageFromType DamageFromType) {
        this(skillId, baseDamage, DamageFromType, false, null);
    }
    
    /**
     * 从技能元数据创建伤害数据
     * @param metaData 技能元数据
     * @param actualDamage 实际伤害值
     * @param isCritical 是否暴击
     */
    public static SkillDamageData fromMetaData(SkillMetaData metaData, double actualDamage, boolean isCritical) {
        return new SkillDamageData(
            metaData.getSkillId(),
            actualDamage,
            metaData.getDamageFromType(),
            isCritical,
            null
        );
    }
    
    public String getSkillId() {
        return skillId;
    }
    
    public double getBaseDamage() {
        return baseDamage;
    }
    
    public DamageFromType getDamageFromType() {
        return damageFromType;
    }
    
    public boolean isCritical() {
        return isCritical;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public EnumMap<AttributeType, Double> getSkillModifiers() {
        return skillModifiers;
    }
    
    /**
     * 添加元数据
     * @param key 键
     * @param value 值
     */
    public void addMetadata(String key, Object value) {
        metadata.put(key, value);
    }
    
    /**
     * 获取元数据
     * @param key 键
     * @param type 类型
     * @param <T> 返回类型
     * @return 值，如果不存在或类型不匹配返回null
     */
    @SuppressWarnings("unchecked")
    public <T> T getMetadata(String key, Class<T> type) {
        Object value = metadata.get(key);
        if (value != null && type.isInstance(value)) {
            return (T) value;
        }
        return null;
    }
    
    /**
     * 计算最终伤害
     * @param caster 施法者
     * @param target 目标
     * @return 计算后的伤害值
     */
    public double calculateFinalDamage(LivingEntity caster, LivingEntity target) {
        double damage = baseDamage;
        
        // 如果是暴击，应用暴击倍率
        if (isCritical) {
            damage *= 1.5; // 默认暴击倍率，可以从配置或技能数据中获取
        }
        
        return damage;
    }
}