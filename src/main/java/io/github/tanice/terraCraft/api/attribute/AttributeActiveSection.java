package io.github.tanice.terraCraft.api.attribute;

import java.io.Serializable;

public enum AttributeActiveSection implements Serializable {
    /* 每秒激活 */
    TIMER,
    /* 伤害计算前 */
    BEFORE_DAMAGE,  // 例：闪避

    /* 伤害计算内 */
    BASE,       // 白值计算区（AttributeType类相加）
    ADD,        // 加算区（同AttributeType类相加）
    MULTIPLY,   // 乘算区（同AttributeType类相加）
    FIX,        // 修正区（按照MULTIPLY的方法计算）

    /* 攻击伤害计算完成后，防御计算前 */
    BETWEEN_DAMAGE_ADN_DEFENCE,
    /* 伤害计算后 */
    AFTER_DAMAGE,  // 例：吸血

    /* 其他 */
    ERROR, INNER,
}
