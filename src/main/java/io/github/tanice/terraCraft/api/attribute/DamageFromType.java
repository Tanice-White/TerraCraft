package io.github.tanice.terraCraft.api.attribute;

import java.io.Serializable;

public enum DamageFromType implements Serializable {
    MELEE,  // 战士
    MAGIC,  // 法师
    RANGED, // 射手
    ROUGE,  // 盗贼
    SUMMON, // 召唤
    OTHER,  // 无职业
    /* 环境 */
    ENVIRONMENT,
}
