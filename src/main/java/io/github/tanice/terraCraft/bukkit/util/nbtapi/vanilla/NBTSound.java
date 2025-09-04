package io.github.tanice.terraCraft.bukkit.util.nbtapi.vanilla;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.github.tanice.terraCraft.core.util.logger.TerraCraftLogger;
import io.github.tanice.terraCraft.core.util.namespace.TerraNamespaceKey;
import org.bukkit.configuration.ConfigurationSection;


import javax.annotation.Nullable;
import java.util.Objects;

public class NBTSound {
    @Nullable
    protected final Float range;
    protected final TerraNamespaceKey id;

    public NBTSound(@Nullable Float range, TerraNamespaceKey id) {
        this.range = range;
        this.id = id;
    }

    public void addToCompound(ReadWriteNBT compound) {
        if (range != null) compound.setFloat("range", range);
        compound.setString("sound_id", id.get());
    }

    @Nullable
    public static NBTSound form(@Nullable ConfigurationSection cfg) {
        if (cfg == null) return null;
        Float range = cfg.isSet("range") ? (float) cfg.getDouble("range") : null;
        TerraNamespaceKey nk = TerraNamespaceKey.from(cfg.getString("id"));
        if (nk == null) {
            TerraCraftLogger.warning("Invalid sound id " + cfg.getString("id"));
            return null;
        }
        return new NBTSound(range, nk);
    }

    @Nullable
    public Float getRange() {
        return this.range;
    }

    public String getId() {
        return this.id.get();
    }

    @Override
    public int hashCode() {
        return Objects.hash(range, id);
    }
}
