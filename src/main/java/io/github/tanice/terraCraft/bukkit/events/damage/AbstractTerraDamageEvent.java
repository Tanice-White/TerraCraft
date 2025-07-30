package io.github.tanice.terraCraft.bukkit.events.damage;

import io.github.tanice.terraCraft.bukkit.events.AbstractTerraEvent;
import io.github.tanice.terraCraft.bukkit.utils.annotation.NonnullByDefault;
import org.bukkit.entity.LivingEntity;

@NonnullByDefault
public abstract class AbstractTerraDamageEvent extends AbstractTerraEvent {

    protected LivingEntity attacker;
    protected boolean critical;

    public AbstractTerraDamageEvent(LivingEntity attacker, LivingEntity defender, boolean isOriCritical) {
        super(defender);
        this.attacker = attacker;
        this.critical = isOriCritical;
    }

    public LivingEntity getAttacker() {
        return attacker;
    }
}
