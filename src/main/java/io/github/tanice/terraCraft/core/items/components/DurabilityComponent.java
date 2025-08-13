package io.github.tanice.terraCraft.core.items.components;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.TerraDurabilityComponent;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

// TODO 支持 weapon 的 攻击耐久减少 和 tool 的挖掘耐久减少
public class DurabilityComponent implements TerraDurabilityComponent {
    @Nullable
    private Integer damage;
    private int maxDamage;
    @Nullable
    private Boolean breakLoss;

    public DurabilityComponent(int maxDamage, @Nullable Boolean breakLoss) {
        this(null, maxDamage, breakLoss);
    }

    public DurabilityComponent(@Nullable Integer damage, int maxDamage, @Nullable Boolean breakLoss) {
        this.damage = damage;
        this.maxDamage = maxDamage;
        this.breakLoss = breakLoss;
    }

    @Nullable
    public static DurabilityComponent from(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            return NBT.getComponents(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(COMPONENT_KEY + "." + MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".durability");
                if (data == null) return null;
                return new DurabilityComponent(data.getInteger("damage"), data.getInteger("max_damage"), data.getBoolean("break_loss"));
            });
        } else {
            return NBT.get(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(TAG_KEY + "." + TERRA_COMPONENT_KEY + ".durability");
                if (data == null) return null;
                return new DurabilityComponent(data.getInteger("damage"), data.getInteger("max_damage"), data.getBoolean("break_loss"));
            });
        }
    }


    @Override
    public void apply(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                ReadWriteNBT component = nbt.getOrCreateCompound(COMPONENT_KEY);
                component.getOrCreateCompound(MINECRAFT_PREFIX + "unbreakable");
                /* 操作custom_data部分 */
                ReadWriteNBT data = component.resolveOrCreateCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + "." + "durability");
                if (damage != null) data.setInteger("damage", damage);
                data.setInteger("max_damage", maxDamage);
                if (breakLoss != null) data.setBoolean("break_loss", breakLoss);
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt -> {
                ReadWriteNBT component = nbt.getOrCreateCompound(TAG_KEY);
                component.setBoolean("Unbreakable", true);

                ReadWriteNBT data = component.resolveOrCreateCompound(TERRA_COMPONENT_KEY + ".durability");
                if (damage != null) data.setInteger("damage", damage);
                data.setInteger("max_damage", maxDamage);
                if (breakLoss != null) data.setBoolean("break_loss", breakLoss);
            });
        }
    }

    @Override
    public int getDamage() {
        return this.damage == null ? 0 : this.damage;
    }

    @Override
    public void setDamage(int damage) {
        this.damage = damage;
    }

    @Override
    public int getMaxDamage() {
        return this.maxDamage;
    }

    @Override
    public void setMaxDamage(int maxDamage) {
        this.maxDamage = maxDamage;
    }

    @Override
    public boolean isBreakLoss() {
        return this.breakLoss != null && breakLoss;
    }

    @Override
    public void setBreakLoss(boolean breakLoss) {
        this.breakLoss = breakLoss;
    }
}
