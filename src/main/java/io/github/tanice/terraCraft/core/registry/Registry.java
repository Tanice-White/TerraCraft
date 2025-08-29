package io.github.tanice.terraCraft.core.registry;

import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;

import javax.annotation.Nullable;
import java.util.concurrent.ConcurrentHashMap;

public final class Registry<T> {
    private final ConcurrentHashMap<String, T> registryMap = new ConcurrentHashMap<>();

    public void register(String key, T value) {
        registryMap.put(key, value);
    }

    @Nullable
    public T get(String key) {
        return registryMap.get(key);
    }

    public static Registry<TerraCalculableMeta> ORI_ITEM = new Registry<>();
    public static Registry<TerraCalculableMeta> ORI_POTION = new Registry<>();
    // public static Registry<TerraCalculableMeta> ORI_ENCHANT = new Registry<>();
    public static Registry<TerraCalculableMeta> ORI_LIVING_ENTITY = new Registry<>();
}
