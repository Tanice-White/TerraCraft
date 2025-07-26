package io.github.tanice.terraCraft.bukkit.events.entity;

import io.github.tanice.terraCraft.api.buffs.TerraBaseBuff;
import io.github.tanice.terraCraft.bukkit.events.AbstractTerraEvent;
import io.github.tanice.terraCraft.bukkit.utils.annotation.NonnullByDefault;
import org.bukkit.entity.LivingEntity;

import java.util.List;

@NonnullByDefault
public class TerraBuffUpdateEvent extends AbstractTerraEvent {

    private final List<TerraBaseBuff> toActivate;
    private final List<TerraBaseBuff> toDeactivate;

    public TerraBuffUpdateEvent(LivingEntity entity, List<TerraBaseBuff> toActivate, List<TerraBaseBuff> toDeactivate) {
        super(entity);
        this.toActivate = toActivate;
        this.toDeactivate = toDeactivate;
    }

    public List<TerraBaseBuff> getToActivateBuffs() {
        return this.toActivate;
    }

    public List<TerraBaseBuff> getToDeactivateBuffs() {
        return this.toDeactivate;
    }
}
