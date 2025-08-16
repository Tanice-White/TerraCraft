package io.github.tanice.terraCraft.core.utils.namespace;

import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;

import javax.annotation.Nullable;

public class TerraNamespaceKey {
    private final String namespace;
    private final String key;

    public static TerraNamespaceKey minecraft(String key) {
        return new TerraNamespaceKey("minecraft", key);
    }

    public TerraNamespaceKey(String key) {
        this.namespace = TerraCraftBukkit.inst().getName();
        this.key = key;
    }

    public TerraNamespaceKey(String namespace, String key) {
        this.namespace = namespace;
        this.key = key;
    }

    @Nullable
    public static TerraNamespaceKey from(String namespaceKey) {
        if (namespaceKey == null) return null;
        if (namespaceKey.contains(":")) {
            String[] split = namespaceKey.split(":");
            return new TerraNamespaceKey(split[0], split[1]);
        } else return new TerraNamespaceKey(namespaceKey);
    }

    public String get() {
        return this.namespace + ":" + this.key;
    }
}
