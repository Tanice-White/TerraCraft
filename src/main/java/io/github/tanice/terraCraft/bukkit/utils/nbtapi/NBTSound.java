package io.github.tanice.terraCraft.bukkit.utils.nbtapi;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.github.tanice.terraCraft.core.utils.namespace.TerraNamespaceKey;


import javax.annotation.Nullable;

public class NBTSound {
    @Nullable
    protected final Float range;
    protected final TerraNamespaceKey soundNamespaceKey;

    public NBTSound(@Nullable Float range, TerraNamespaceKey SoundNamespaceKey) {
        this.range = range;
        this.soundNamespaceKey = SoundNamespaceKey;
    }

    public void addToCompound(ReadWriteNBT compound) {
        if (range != null) compound.setFloat("range", range);
        compound.setString("sound_id", soundNamespaceKey.get());
    }

    @Nullable
    public Float getRange() {
        return this.range;
    }

    public String getId() {
        return this.soundNamespaceKey.get();
    }
}
