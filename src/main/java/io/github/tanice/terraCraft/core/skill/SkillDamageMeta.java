package io.github.tanice.terraCraft.core.skill;

/**
 * 技能伤害数据，包含技能造成伤害所需的所有信息
 */
public class SkillDamageMeta {
    /** 玩家面板伤害比例 */
    private double damageK;
    /** 固定伤害 */
    private double damage;
    /** 是否会被武器的伤害类型增伤影响 */
    private boolean powerByDamageType;
    /** 能否暴击 */
    private boolean canCritical;
    /** 暴击率是玩家暴击率的比例 */
    private double criticalK;
    /** 固定的暴击率 */
    private double criticalChance;
    /** 是否无视护甲 */
    private boolean ignoreArmor;
    /** 是否不造成击退 */
    private boolean preventKnockback;
    /** 攻击是否不产生无敌帧 */
    private boolean preventImmunity;
    /** 攻击是否忽略玩家的无敌帧 */
    private boolean ignoreInvulnerability;

    // 所有参数的构造函数
    public SkillDamageMeta(double damageK, double damage, boolean powerByDamageType,
                           boolean canCritical, double criticalK, double criticalChance,
                           boolean ignoreArmor, boolean preventKnockback, boolean preventImmunity,
                           boolean ignoreInvulnerability) {
        this.damageK = damageK;
        this.damage = damage;
        this.powerByDamageType = powerByDamageType;
        this.canCritical = canCritical;
        this.criticalK = criticalK;
        this.criticalChance = criticalChance;
        this.ignoreArmor = ignoreArmor;
        this.preventKnockback = preventKnockback;
        this.preventImmunity = preventImmunity;
        this.ignoreInvulnerability = ignoreInvulnerability;
    }

    public double getDamageK() {
        return damageK;
    }

    public void setDamageK(double damageK) {
        this.damageK = damageK;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public boolean isPowerByDamageType() {
        return powerByDamageType;
    }

    public void setPowerByDamageType(boolean powerByDamageType) {
        this.powerByDamageType = powerByDamageType;
    }

    public boolean canCritical() {
        return canCritical;
    }

    public void setCritical(boolean canCritical) {
        this.canCritical = canCritical;
    }

    public double getCriticalK() {
        return criticalK;
    }

    public void setCriticalK(double criticalK) {
        this.criticalK = criticalK;
    }

    public double getCriticalChance() {
        return criticalChance;
    }

    public void setCriticalChance(double criticalChance) {
        this.criticalChance = criticalChance;
    }

    public boolean isIgnoreArmor() {
        return ignoreArmor;
    }

    public void setIgnoreArmor(boolean ignoreArmor) {
        this.ignoreArmor = ignoreArmor;
    }

    public boolean isPreventKnockback() {
        return preventKnockback;
    }

    public void setPreventKnockback(boolean preventKnockback) {
        this.preventKnockback = preventKnockback;
    }

    public boolean isPreventImmunity() {
        return preventImmunity;
    }

    public void setPreventImmunity(boolean preventImmunity) {
        this.preventImmunity = preventImmunity;
    }

    public boolean isIgnoreInvulnerability() {
        return ignoreInvulnerability;
    }

    public void setIgnoreInvulnerability(boolean ignoreInvulnerability) {
        this.ignoreInvulnerability = ignoreInvulnerability;
    }
}