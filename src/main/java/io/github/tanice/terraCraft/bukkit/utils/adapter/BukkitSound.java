package io.github.tanice.terraCraft.bukkit.utils.adapter;

import io.github.tanice.terraCraft.core.utils.namespace.TerraNamespaceKey;

public class BukkitSound {
    protected final float range;
    protected final TerraNamespaceKey SoundNamespaceKey;

    public BukkitSound(float range, TerraNamespaceKey SoundNamespaceKey) {
        this.range = range;
        this.SoundNamespaceKey = SoundNamespaceKey;
    }

    public float getRange() {
        return this.range;
    }

    public String getId() {
        return this.SoundNamespaceKey.get();
    }
}
