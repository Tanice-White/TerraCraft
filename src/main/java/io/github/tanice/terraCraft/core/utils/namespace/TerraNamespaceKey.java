package io.github.tanice.terraCraft.core.utils.namespace;

import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;

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

    public String get() {
        return this.namespace + ":" + this.key;
    }
}
