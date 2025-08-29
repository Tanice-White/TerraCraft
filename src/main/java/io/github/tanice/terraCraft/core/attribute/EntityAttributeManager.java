package io.github.tanice.terraCraft.core.attribute;

import io.github.tanice.terraCraft.api.attribute.TerraEntityAttributeManager;
import io.github.tanice.terraCraft.api.attribute.calculator.TerraAttributeCalculator;
import io.github.tanice.terraCraft.bukkit.util.TerraWeakReference;
import io.github.tanice.terraCraft.bukkit.util.scheduler.TerraSchedulers;
import io.github.tanice.terraCraft.core.calculator.EntityAttributeCalculator;
import io.github.tanice.terraCraft.core.config.ConfigManager;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import org.bukkit.entity.LivingEntity;

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
    private final ConcurrentMap<TerraWeakReference, AtomicBoolean> computingFlags;
    /** 脏标记 */
    private final ConcurrentMap<TerraWeakReference, AtomicBoolean> dirtyFlags;
    /** 计算结果-实体属性 */
    private final ConcurrentMap<TerraWeakReference, TerraAttributeCalculator> calculatorMap;

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
        TerraWeakReference reference = new TerraWeakReference(entity);
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
        TerraWeakReference reference = new TerraWeakReference(entity);
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
        TerraWeakReference reference = new TerraWeakReference(entity);
        computingFlags.remove(reference);
        dirtyFlags.remove(reference);
        calculatorMap.remove(reference);
    }

    /**
     * 处理实体更新
     */
    private void processAttributeUpdate(TerraWeakReference reference) {
        try {
            LivingEntity entity = reference.get();
            if (entity != null && entity.isValid()) {
                /* 计算前清除脏标记 */
                dirtyFlags.get(reference).set(false);
                calculatorMap.put(reference, new EntityAttributeCalculator(entity));
            }
        } finally {
            computingFlags.get(reference).set(false);
            /* 如果期间有新请求，继续处理 */
            if (dirtyFlags.get(reference).getAndSet(false)) {
                if (computingFlags.get(reference).compareAndSet(false, true)) {
                    /* 异步线程->避免递归的栈溢出 */
                    TerraSchedulers.async().run(() -> processAttributeUpdate(reference));
                }
            }
        }
    }

    private void cleanup() {
        Iterator<Map.Entry<TerraWeakReference, TerraAttributeCalculator>> it = calculatorMap.entrySet().iterator();
        Map.Entry<TerraWeakReference, TerraAttributeCalculator> entry;
        TerraWeakReference reference;
        LivingEntity entity;
        while (it.hasNext()) {
            entry = it.next();
            reference = entry.getKey();
            entity = reference.get();
            if (entity != null && entity.isValid()) continue;
            it.remove();
            computingFlags.remove(reference);
            dirtyFlags.remove(reference);
        }
    }
}
