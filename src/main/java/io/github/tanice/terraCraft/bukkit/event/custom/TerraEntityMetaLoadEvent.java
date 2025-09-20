package io.github.tanice.terraCraft.bukkit.event.custom;

import io.github.tanice.terraCraft.bukkit.util.annotation.NonnullByDefault;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.HandlerList;

@NonnullByDefault
public class TerraEntityMetaLoadEvent extends AbstractTerraMetaLoadEvent {

    private final LivingEntity entity;

    public TerraEntityMetaLoadEvent(LivingEntity entity) {
        super();
        this.entity = entity;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
