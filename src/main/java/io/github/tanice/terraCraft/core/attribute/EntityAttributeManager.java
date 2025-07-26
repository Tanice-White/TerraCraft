package io.github.tanice.terraCraft.core.attribute;

import io.github.tanice.terraCraft.api.attribute.TerraEntityAttributeManager;
import io.github.tanice.terraCraft.api.attribute.calculator.TerraAttributeCalculator;
import io.github.tanice.terraCraft.api.service.TerraCached;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.events.entity.TerraAttributeUpdateEvent;
import io.github.tanice.terraCraft.bukkit.utils.events.TerraEvents;
import io.github.tanice.terraCraft.bukkit.utils.scheduler.Schedulers;
import io.github.tanice.terraCraft.core.attribute.calculator.EntityAttributeCalculator;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventPriority;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 实体属性异步计算
 */
public class EntityAttributeManager implements TerraEntityAttributeManager {
    /** 标记实体是否正在计算中 */
    protected final ConcurrentMap<UUID, AtomicBoolean> computingFlags;
    /** 脏标记 */
    protected final ConcurrentMap<UUID, AtomicBoolean> dirtyFlags;
    /** 计算结果-实体属性 */
    private final ConcurrentMap<UUID, TerraAttributeCalculator> calculatorMap;

    public EntityAttributeManager() {
        computingFlags = new ConcurrentHashMap<>();
        dirtyFlags = new ConcurrentHashMap<>();
        calculatorMap = new ConcurrentHashMap<>();

        TerraEvents.subscribe(TerraAttributeUpdateEvent.class)
                .ignoreCancelled(true)
                .priority(EventPriority.HIGH).handler(event -> this.updateAttribute(event.getEntity()))
                .register();
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
            Schedulers.async().run(() -> asyncRun(uuid));

            if (TerraCraftBukkit.inst().getConfigManager().isDebug()) {
                TerraCraftLogger.debug(TerraCraftLogger.DebugLevel.CALCULATOR, "Entity: " + entity.getName() + " attribute updating");
            }
            /* 计算中，标记为脏 */
        } else dirtyFlags.get(uuid).set(true);
    }

    /**
     * 处理实体更新
     */
    private void asyncRun(UUID uuid) {
        try {
            TerraCached cached = TerraCraftBukkit.inst().getCacheService().get(uuid);
            if (cached == null) {
                calculatorMap.remove(uuid);
                return;
            }
            /* 计算前清除脏标记 */
            dirtyFlags.get(uuid).set(false);
            calculatorMap.put(uuid, new EntityAttributeCalculator(cached.getEntity()));

        } finally {
            computingFlags.get(uuid).set(false);
            /* 如果期间有新请求，继续处理 */
            if (dirtyFlags.get(uuid).getAndSet(false)) {
                if (computingFlags.get(uuid).compareAndSet(false, true)) {
                    Schedulers.async().run(() -> asyncRun(uuid));
                }
            }
        }
    }
}
