package io.github.tanice.terraCraft.core.service;

import io.github.tanice.terraCraft.api.service.TerraCached;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class Cache implements TerraCached {
    private final UUID uuid;
    private final AtomicReference<LivingEntity> entityReference;
    private final boolean isPlayer;

    public Cache(LivingEntity entity) {
        this.uuid = entity.getUniqueId();
        this.entityReference = new AtomicReference<>(entity);
        this.isPlayer = entity instanceof Player;
    }

    @Override
    public UUID getEntityId() {
        return this.uuid;
    }

    @Override
    public boolean isPlayer() {
        return this.isPlayer;
    }

    @Override
    public boolean isValid() {
        LivingEntity e = this.entityReference.get();
        return e.isValid() && !e.isEmpty() && !e.isDead();
    }

    @Override
    public LivingEntity getEntity() {
        return this.entityReference.get();
    }
}
