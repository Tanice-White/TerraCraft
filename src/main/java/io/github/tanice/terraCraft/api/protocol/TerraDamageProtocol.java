package io.github.tanice.terraCraft.api.protocol;

import io.github.tanice.terraCraft.api.attribute.DamageFromType;
import io.github.tanice.terraCraft.api.attribute.calculator.TerraAttributeCalculator;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nullable;

public final class TerraDamageProtocol {
    @Nullable
    private final LivingEntity attacker;
    private final LivingEntity defender;
    @Nullable
    private final TerraAttributeCalculator attackerCalculator;
    private final TerraAttributeCalculator defenderCalculator;
    private final DamageFromType weaponDamageType;

    private double finalDamage;
    // TODO Skill相关
    /** 是否判定击中 */
    private boolean hit;

    public TerraDamageProtocol(
            @Nullable LivingEntity attacker, LivingEntity defender,
            @Nullable TerraAttributeCalculator attackerCalculator, TerraAttributeCalculator defenderCalculator,
            DamageFromType weaponDamageType, double damage
    ) {
        this.attacker = attacker;
        this.defender = defender;
        this.attackerCalculator = attackerCalculator;
        this.defenderCalculator = defenderCalculator;
        this.weaponDamageType = weaponDamageType;
        this.finalDamage = damage;
        this.hit = true;
    }

    public @Nullable LivingEntity getAttacker() {
        return attacker;
    }

    public LivingEntity getDefender() {
        return defender;
    }

    public @Nullable TerraAttributeCalculator getAttackerCalculator() {
        return attackerCalculator;
    }

    public TerraAttributeCalculator getDefenderCalculator() {
        return defenderCalculator;
    }

    public DamageFromType getWeaponDamageType() {
        return weaponDamageType;
    }

    public double getFinalDamage() {
        return finalDamage;
    }

    public void setFinalDamage(double finalDamage) {
        this.finalDamage = finalDamage;
    }

    public boolean isHit() {
        return hit;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }
}
