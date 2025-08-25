package io.github.tanice.terraCraft.core.attribute;

import io.github.tanice.terraCraft.api.attribute.TerraEntityAttributeManager;
import io.github.tanice.terraCraft.api.attribute.calculator.TerraAttributeCalculator;
import io.github.tanice.terraCraft.bukkit.util.scheduler.TerraSchedulers;
import io.github.tanice.terraCraft.core.calculator.EntityAttributeCalculator;
import io.github.tanice.terraCraft.core.config.ConfigManager;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import org.bukkit.entity.LivingEntity;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 实体属性异步计算
 */
public class EntityAttributeManager implements TerraEntityAttributeManager {
    private static final int CLEAN_UP_CD = 9;

    /** 标记实体是否正在计算中 */
    private final ConcurrentMap<WeakReference<LivingEntity>, AtomicBoolean> computingFlags;
    /** 脏标记 */
    private final ConcurrentMap<WeakReference<LivingEntity>, AtomicBoolean> dirtyFlags;
    /** 计算结果-实体属性 */
    private final ConcurrentMap<WeakReference<LivingEntity>, TerraAttributeCalculator> calculatorMap;

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
        WeakReference<LivingEntity> reference = new WeakReference<>(entity);
        TerraAttributeCalculator calculator = calculatorMap.get(reference);
        /* 没有则现场计算 */
        if (calculator == null) {
            EntityAttributeCalculator nc = new EntityAttributeCalculator(entity);
            calculatorMap.put(reference, nc);
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
        WeakReference<LivingEntity> reference = new WeakReference<>(entity);
        dirtyFlags.computeIfAbsent(reference, k -> new AtomicBoolean(false));

        AtomicBoolean computing = computingFlags.computeIfAbsent(reference, k -> new AtomicBoolean(false));
        if (computing.compareAndSet(false, true)) {
            TerraSchedulers.async().run(() -> processAttributeUpdate(reference));

            if (ConfigManager.isDebug()) {
                TerraCraftLogger.debug(TerraCraftLogger.DebugLevel.CALCULATOR, "Entity: " + entity.getName() + " attribute updating");
            }
            /* 计算中，标记为脏 */
        } else dirtyFlags.get(reference).set(true);
    }

    @Override
    public void unregister(LivingEntity entity) {
        WeakReference<LivingEntity> reference = new WeakReference<>(entity);
        computingFlags.remove(reference);
        dirtyFlags.remove(reference);
        calculatorMap.remove(reference);
    }

    /**
     * 处理实体更新
     */
    private void processAttributeUpdate(WeakReference<LivingEntity> reference) {
        try {
            LivingEntity e = reference.get();
            if (e != null && e.isValid()) {
                /* 计算前清除脏标记 */
                dirtyFlags.get(reference).set(false);
                calculatorMap.put(reference, new EntityAttributeCalculator(e));
            }
        } finally {
            computingFlags.get(reference).set(false);
            /* 如果期间有新请求，继续处理 */
            if (dirtyFlags.get(reference).getAndSet(false)) {
                if (computingFlags.get(reference).compareAndSet(false, true)) {
                    TerraSchedulers.async().run(() -> processAttributeUpdate(reference));
                }
            }
        }
    }

    private void cleanup() {
        Iterator<Map.Entry<WeakReference<LivingEntity>, TerraAttributeCalculator>> it = calculatorMap.entrySet().iterator();
        Map.Entry<WeakReference<LivingEntity>, TerraAttributeCalculator> entry;
        LivingEntity e;
        while (it.hasNext()) {
            entry = it.next();
            e = entry.getKey().get();
            if (e != null && e.isValid()) continue;
            computingFlags.remove(entry.getKey());
            dirtyFlags.remove(entry.getKey());
            calculatorMap.remove(entry.getKey());
        }
    }
}
