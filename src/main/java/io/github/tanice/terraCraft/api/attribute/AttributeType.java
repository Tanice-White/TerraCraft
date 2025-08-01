package io.github.tanice.terraCraft.api.attribute;

import java.io.Serializable;

public enum AttributeType implements Serializable {
    ATTACK_DAMAGE,
    ARMOR,
    ARMOR_TOUGHNESS,        // 加算
    CRITICAL_STRIKE_CHANCE, // 加算
    CRITICAL_STRIKE_DAMAGE, // 加算
    PRE_ARMOR_REDUCTION,    // 属性单独提供，加算  很少，对计算影响很大  受平衡影响
    AFTER_ARMOR_REDUCTION,  // 属性单独提供，加算  基本的减伤就是此类型  受平衡影响
    SKILL_MANA_COST,        // 加算
    SKILL_COOLDOWN,         // 加算
}
