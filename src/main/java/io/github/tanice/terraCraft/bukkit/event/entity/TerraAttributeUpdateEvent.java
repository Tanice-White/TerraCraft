package io.github.tanice.terraCraft.bukkit.event.entity;

import io.github.tanice.terraCraft.bukkit.event.AbstractTerraEvent;
import io.github.tanice.terraCraft.bukkit.util.annotation.NonnullByDefault;
import org.bukkit.entity.LivingEntity;

@NonnullByDefault
public class TerraAttributeUpdateEvent extends AbstractTerraEvent {

    public TerraAttributeUpdateEvent(LivingEntity entity) {
        super(entity);
    }
}
