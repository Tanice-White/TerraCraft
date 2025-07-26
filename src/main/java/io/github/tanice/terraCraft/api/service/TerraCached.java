package io.github.tanice.terraCraft.api.service;

import org.bukkit.entity.LivingEntity;

import java.util.UUID;

public interface TerraCached {
    UUID getEntityId();

    boolean isPlayer();

    boolean isValid();

    LivingEntity getEntity();
}
