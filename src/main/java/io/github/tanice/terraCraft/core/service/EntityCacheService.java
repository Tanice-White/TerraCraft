package io.github.tanice.terraCraft.core.service;

import io.github.tanice.terraCraft.api.service.TerraCacheService;
import io.github.tanice.terraCraft.api.service.TerraCached;
import io.github.tanice.terraCraft.bukkit.utils.scheduler.TerraSchedulers;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class EntityCacheService implements TerraCacheService {
    /** 实体状态缓存 */
    private final ConcurrentMap<UUID, TerraCached> entityCache;

    public EntityCacheService() {
        entityCache = new ConcurrentHashMap<>();
        TerraSchedulers.async().repeat(this::cleanUp, 1, 20);
    }

    public void reload() {
    }

    public void unload() {
        entityCache.clear();
    }

    @Override
    public void remove(UUID uuid) {
        entityCache.remove(uuid);
    }

    @Override
    public TerraCached get(UUID uuid) {
        return entityCache.get(uuid);
    }

    private void cleanUp() {
        entityCache.entrySet().removeIf(entry -> {
            TerraCached cached = entry.getValue();
            return !cached.isValid();
        });
    }
}
