package io.github.tanice.terraCraft.bukkit.utils.nbtapi;

import io.github.tanice.terraCraft.core.utils.namespace.TerraNamespaceKey;


import javax.annotation.Nonnull;

public class NBTSound {
    protected final Float range;
    @Nonnull
    protected final TerraNamespaceKey SoundNamespaceKey;

    public NBTSound(Float range, @Nonnull TerraNamespaceKey SoundNamespaceKey) {
        this.range = range;
        this.SoundNamespaceKey = SoundNamespaceKey;
    }

    public Float getRange() {
        return this.range;
    }

    public String getId() {
        return this.SoundNamespaceKey.get();
    }
}
