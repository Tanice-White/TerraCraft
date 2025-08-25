package io.github.tanice.terraCraft.core.util.helper.mythicmobs;

import io.github.tanice.terraCraft.core.calculator.DamageCalculator;
import io.github.tanice.terraCraft.core.skill.SkillDamageMeta;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderBoolean;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;


public class TerraDamageMechanic implements ITargetedEntitySkill {

    protected PlaceholderDouble damageK;
    protected PlaceholderDouble damage;
    protected PlaceholderBoolean powerByDamageType;
    protected PlaceholderBoolean canCritical;
    protected PlaceholderDouble criticalK;
    protected PlaceholderDouble criticalChance;
    protected PlaceholderBoolean ignoreArmor;
    protected PlaceholderBoolean preventKnockback;
    protected PlaceholderBoolean preventImmunity;
    protected PlaceholderBoolean ignoreInvulnerability;

    public TerraDamageMechanic(MythicLineConfig mlc) {
        this.damageK = mlc.getPlaceholderDouble(new String[]{"damageK", "dk"}, 1D);
        this.damage = mlc.getPlaceholderDouble(new String[]{"damage", "d"}, -1D);
        this.powerByDamageType = mlc.getPlaceholderBoolean(new String[]{"powerByDamageType", "p"}, true);
        this.canCritical = mlc.getPlaceholderBoolean(new String[]{"critical", "c"}, true);
        this.criticalK = mlc.getPlaceholderDouble(new String[]{"criticalK", "ck"}, 1D);
        this.criticalChance = mlc.getPlaceholderDouble(new String[]{"criticalChance", "cc"}, -1D);
        this.ignoreArmor = mlc.getPlaceholderBoolean(new String[]{"ignoreArmor", "ia"}, false);
        this.preventKnockback = mlc.getPlaceholderBoolean(new String[]{"preventKnockback", "pk"}, false);
        this.preventImmunity = mlc.getPlaceholderBoolean(new String[]{"preventImmunity", "pi"}, false);
        this.ignoreInvulnerability = mlc.getPlaceholderBoolean(new String[]{"ignoreInvulnerability", "ii"}, false);
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        if (target.isDead() || !target.isLiving() || target.getHealth() <= 0.0) return SkillResult.INVALID_TARGET;

        Entity entityAttacker = data.getCaster().getEntity().getBukkitEntity();
        Entity entityDefender = target.getBukkitEntity();

        if (entityAttacker instanceof LivingEntity attacker && entityDefender instanceof LivingEntity defender) {
            SkillDamageMeta skillDamageData = new SkillDamageMeta(
                    damageK.get(data, target),
                    damage.get(data, target),
                    powerByDamageType.get(data, target),
                    canCritical.get(data, target),
                    criticalK.get(data, target),
                    criticalChance.get(data, target),
                    ignoreArmor.get(data, target),
                    preventKnockback.get(data, target),
                    preventImmunity.get(data, target),
                    ignoreInvulnerability.get(data, target)
            );
            target.damage((float) DamageCalculator.calculate(attacker, defender, skillDamageData).getFinalDamage());
        }
        return SkillResult.SUCCESS;
    }
}