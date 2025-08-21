package io.github.tanice.terraCraft.core.attribute;

import io.github.tanice.terraCraft.api.attribute.TerraEntityAttributeManager;
import io.github.tanice.terraCraft.api.attribute.calculator.TerraAttributeCalculator;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.utils.scheduler.TerraSchedulers;
import io.github.tanice.terraCraft.core.calculator.EntityAttributeCalculator;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 实体属性异步计算
 */
public class EntityAttributeManager implements TerraEntityAttributeManager {
    private static final int CLEAN_UP_CD = 9;

    /** 标记实体是否正在计算中 */
    private final ConcurrentMap<UUID, AtomicBoolean> computingFlags;
    /** 脏标记 */
    private final ConcurrentMap<UUID, AtomicBoolean> dirtyFlags;
    /** 计算结果-实体属性 */
    private final ConcurrentMap<UUID, TerraAttributeCalculator> calculatorMap;

    public EntityAttributeManager() {
        computingFlags = new ConcurrentHashMap<>();
        dirtyFlags = new ConcurrentHashMap<>();
        calculatorMap = new ConcurrentHashMap<>();

        TerraSchedulers.async().repeat(this::cleanup, 1, CLEAN_UP_CD);
    }

    public void reload() {
        computingFlags.clear();
        dirtyFlags.clear();
        calculatorMap.clear();
    }

    public void unload() {
        computingFlags.clear();
        dirtyFlags.clear();
        calculatorMap.clear();
    }

    @Override
    public TerraAttributeCalculator getAttributeCalculator(LivingEntity entity) {
        UUID uuid = entity.getUniqueId();
        TerraAttributeCalculator calculator = calculatorMap.get(uuid);
        /* 没有则现场计算 */
        if (calculator == null) {
            EntityAttributeCalculator nc = new EntityAttributeCalculator(entity);
            calculatorMap.put(uuid, nc);
            return nc;
        }
        return calculator;
    }

    @Override
    public int getManagedEntityCount() {
        return calculatorMap.size();
    }

    @Override
    public void updateAttribute(LivingEntity entity) {
        UUID uuid = entity.getUniqueId();
        dirtyFlags.computeIfAbsent(uuid, k -> new AtomicBoolean(false));

        AtomicBoolean computing = computingFlags.computeIfAbsent(uuid, k -> new AtomicBoolean(false));
        if (computing.compareAndSet(false, true)) {
            TerraSchedulers.async().run(() -> processAttributeUpdate(uuid));

            if (TerraCraftBukkit.inst().getConfigManager().isDebug()) {
                TerraCraftLogger.debug(TerraCraftLogger.DebugLevel.CALCULATOR, "Entity: " + entity.getName() + " attribute updating");
            }
            /* 计算中，标记为脏 */
        } else dirtyFlags.get(uuid).set(true);
    }

    @Override
    public void unregister(LivingEntity entity) {
        UUID uuid = entity.getUniqueId();
        computingFlags.remove(uuid);
        dirtyFlags.remove(uuid);
        calculatorMap.remove(uuid);
    }

    /**
     * 处理实体更新
     */
    private void processAttributeUpdate(UUID uuid) {
        try {
            Entity e = Bukkit.getEntity(uuid);
            if (e != null && e.isValid()) {
                /* 计算前清除脏标记 */
                dirtyFlags.get(uuid).set(false);
                calculatorMap.put(uuid, new EntityAttributeCalculator((LivingEntity) e));
            }
        } finally {
            computingFlags.get(uuid).set(false);
            /* 如果期间有新请求，继续处理 */
            if (dirtyFlags.get(uuid).getAndSet(false)) {
                if (computingFlags.get(uuid).compareAndSet(false, true)) {
                    TerraSchedulers.async().run(() -> processAttributeUpdate(uuid));
                }
            }
        }
    }

    private void cleanup() {
        Iterator<Map.Entry<UUID, TerraAttributeCalculator>> it = calculatorMap.entrySet().iterator();
        Map.Entry<UUID, TerraAttributeCalculator> entry;
        UUID uuid;
        Entity e;
        while (it.hasNext()) {
            entry = it.next();
            uuid = entry.getKey();
            e = Bukkit.getEntity(uuid);
            if (e != null && e.isValid()) continue;
            computingFlags.remove(uuid);
            dirtyFlags.remove(uuid);
            calculatorMap.remove(uuid);
        }
    }
}
