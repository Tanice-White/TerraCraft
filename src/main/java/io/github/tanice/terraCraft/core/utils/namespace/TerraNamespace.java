package io.github.tanice.terraCraft.core.utils.namespace;

import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;

public class TerraNamespace {
    private final String v;

    public TerraNamespace(String key) {
        v = TerraCraftBukkit.inst().getName() + ":" + key;
    }

    public String get() {
        return v;
    }
}
