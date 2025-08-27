package io.github.tanice.terraCraft.bukkit.util;

import org.bukkit.entity.LivingEntity;

import java.lang.ref.WeakReference;

public final class TerraWeakReference extends WeakReference<LivingEntity> {
    private final int referentHashCode;

    public TerraWeakReference(LivingEntity referent) {
        super(referent);
        this.referentHashCode = referent.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof TerraWeakReference) {
            LivingEntity thisEntity = this.get();
            LivingEntity otherEntity = ((TerraWeakReference) obj).get();
            return thisEntity != null && otherEntity != null && thisEntity.getUniqueId().equals(otherEntity.getUniqueId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return referentHashCode;
    }
}
