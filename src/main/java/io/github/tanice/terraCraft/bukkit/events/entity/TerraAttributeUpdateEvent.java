package io.github.tanice.terraCraft.bukkit.events.entity;

import io.github.tanice.terraCraft.bukkit.events.AbstractTerraEvent;
import io.github.tanice.terraCraft.bukkit.utils.annotation.NonnullByDefault;
import org.bukkit.entity.LivingEntity;


@NonnullByDefault
public class TerraAttributeUpdateEvent extends AbstractTerraEvent {

    public TerraAttributeUpdateEvent(LivingEntity entity) {
        super(entity);
    }
}
