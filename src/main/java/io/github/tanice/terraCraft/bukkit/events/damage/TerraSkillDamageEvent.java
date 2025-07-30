package io.github.tanice.terraCraft.bukkit.events.damage;

import org.bukkit.entity.LivingEntity;

public class TerraSkillDamageEvent extends AbstractTerraDamageEvent{

    public TerraSkillDamageEvent(LivingEntity attacker, LivingEntity defender, boolean isOriCritical) {
        super(attacker, defender, isOriCritical);
    }
}
