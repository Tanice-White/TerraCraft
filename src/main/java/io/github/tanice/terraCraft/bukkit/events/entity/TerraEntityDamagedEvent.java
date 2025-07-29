package io.github.tanice.terraCraft.bukkit.events.entity;

import org.bukkit.entity.LivingEntity;

import javax.annotation.Nullable;


public class TerraEntityDamagedEvent extends AbstractTerraEvent {

    @Nullable
    private final LivingEntity attacker;
    @Nullable
    private final SkillMetaData skillMetaData;
    private final boolean critical;

    public TerraEntityDamagedEvent(LivingEntity attacker, LivingEntity entity, TerraSkillMetaData skillMetaData, boolean critical) {
        super(entity);
        this.attacker = attacker;
        this.skillMetaData = skillMetaData;
        this.critical = critical;
    }

    public LivingEntity getAttacker() {}
}
