package io.github.tanice.terraCraft.api.attribute;

import java.io.Serializable;

public enum AttributeActiveSection implements Serializable {
    /* 每秒激活 */
    TIMER(false, -1),
    /* 伤害计算前 */
    BEFORE_DAMAGE(false, -1),  // 例：闪避

    /* 伤害计算内 */
    BASE(true, 0),       // 白值计算区（AttributeType类相加）
    ADD(true, 1),        // 加算区
    MULTIPLY(true, 2),   // 乘算区
    FIX(true, 3),        // 修正区

    /* 攻击伤害计算完成后，防御计算前 */
    BETWEEN_DAMAGE_AND_DEFENCE(false, -1),
    /* 伤害计算后 */
    AFTER_DAMAGE(false, -1),  // 例：吸血

    /* 其他 */
    ERROR(false, -1),
    INNER(false, -1);

    private final boolean calculable;
    private final int priority;

    // 构造函数
    AttributeActiveSection(boolean calculable, int priority) {
        this.calculable = calculable;
        this.priority = priority;
    }

    /**
     * 判断该区域是否可计算
     * @return 可计算返回true，否则返回false
     */
    public boolean canCalculateInMeta() {
        return calculable;
    }

    /**
     * 获取计算优先级
     * @return 优先级数值，数值越小越先计算
     */
    public int getPriority() {
        return priority;
    }
}
