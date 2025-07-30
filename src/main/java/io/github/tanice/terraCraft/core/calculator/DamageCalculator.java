package io.github.tanice.terraCraft.core.calculator;

import io.github.tanice.terraCraft.bukkit.utils.annotation.NonnullByDefault;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

@NonnullByDefault
public final class DamageCalculator {

    /** 非生物来源的伤害 */
    public static double calculate(LivingEntity defender, double oriDamage) {

    }

    /** 生物来源的伤害 */
    public static double calculate(LivingEntity attacker, LivingEntity defender, @Nullable ItemStack weapon, @Nullable ItemStack projectile, double oriDamage) {
    }

    /** 生物来源的技能伤害 */
    public static double calculate(LivingEntity attacker, LivingEntity defender, TerraSkillMeta skillMeta) {

    }
}
