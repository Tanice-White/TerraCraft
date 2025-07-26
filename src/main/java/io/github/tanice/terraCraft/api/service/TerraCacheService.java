package io.github.tanice.terraCraft.api.service;

import java.util.UUID;

public interface TerraCacheService {
    /**
     * 不需要手动删除
     */
    void remove(UUID uuid);

    /**
     * 获取缓存实体
     */
    TerraCached get(UUID uuid);
}
