package io.github.tanice.terraCraft.bukkit.events.damage;

import org.bukkit.entity.LivingEntity;

public class TerraDamageEvent extends AbstractTerraDamageEvent{

    public TerraDamageEvent(LivingEntity attacker, LivingEntity defender, boolean isOriCritical) {
        super(attacker, defender, isOriCritical);
    }
}
