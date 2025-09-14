package io.github.tanice.terraCraft.api.attribute;

import java.io.Serializable;

public enum AttributeType implements Serializable {
    ATTACK_DAMAGE,
    ARMOR,
    ARMOR_TOUGHNESS,

    CRITICAL_STRIKE_CHANCE,
    CRITICAL_STRIKE_DAMAGE,

    PRE_ARMOR_REDUCTION,
    AFTER_ARMOR_REDUCTION,

    SKILL_MANA_COST,
    SKILL_COOLDOWN,

    MANA_RECOVERY_SPEED,
}
