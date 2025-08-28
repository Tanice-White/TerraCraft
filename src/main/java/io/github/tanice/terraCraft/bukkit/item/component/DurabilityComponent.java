package io.github.tanice.terraCraft.bukkit.item.component;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import io.github.tanice.terraCraft.api.item.component.ComponentState;
import io.github.tanice.terraCraft.api.item.component.TerraBaseComponent;
import io.github.tanice.terraCraft.api.item.component.TerraDurabilityComponent;
import io.github.tanice.terraCraft.api.item.component.AbstractItemComponent;
import io.github.tanice.terraCraft.bukkit.util.version.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.util.version.ServerVersion;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Objects;

import static io.github.tanice.terraCraft.api.command.TerraCommand.*;

// TODO 支持 weapon 的 攻击耐久减少 和 tool 的挖掘耐久减少
public class DurabilityComponent extends AbstractItemComponent implements TerraDurabilityComponent {
    @Nullable
    private Integer damage;
    private int maxDamage;
    @Nullable
    private Boolean breakLoss;

    public DurabilityComponent(int maxDamage, @Nullable Boolean breakLoss, boolean updatable) {
        this(null, maxDamage, breakLoss, updatable);
    }

    public DurabilityComponent(@Nullable Integer damage, int maxDamage, @Nullable Boolean breakLoss, boolean updatable) {
        super(updatable);
        this.damage = damage;
        this.maxDamage = maxDamage;
        this.breakLoss = breakLoss;
    }

    public DurabilityComponent(@Nullable Integer damage, int maxDamage, @Nullable Boolean breakLoss, ComponentState state) {
        super(state);
        this.damage = damage;
        this.maxDamage = maxDamage;
        this.breakLoss = breakLoss;
    }

    public DurabilityComponent(ConfigurationSection cfg) {
        super(cfg.getBoolean("updatable", true));
        this.damage = cfg.isSet("damage") ? cfg.getInt("damage") : null;
        this.maxDamage = cfg.getInt("max_damage", 1);
        this.breakLoss = cfg.isSet("break_loss") ? cfg.getBoolean("break_loss") : null;
    }

    @Nullable
    public static DurabilityComponent from(ItemStack item) {
        if (item == null || item.isEmpty()) return null;
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            return NBT.getComponents(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".durability");
                if (data == null) return null;
                return new DurabilityComponent(data.getInteger("damage"), data.getInteger("max_damage"), data.getBoolean("break_loss"), new ComponentState(data.getByte("state")));
            });
        } else {
            return NBT.get(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(TERRA_COMPONENT_KEY + ".durability");
                if (data == null) return null;
                return new DurabilityComponent(data.getInteger("damage"), data.getInteger("max_damage"), data.getBoolean("break_loss"), new ComponentState(data.getByte("state")));
            });
        }
    }


    @Override
    public void doCover(ItemStack item) {
        clear(item);
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt -> {
                nbt.getOrCreateCompound(MINECRAFT_PREFIX + "unbreakable");
                /* 操作custom_data部分 */
                ReadWriteNBT data = nbt.resolveOrCreateCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".durability");
                addToCompound(data);
            });
        } else {
            NBT.modify(item, nbt -> {
                nbt.setBoolean("Unbreakable", true);
                ReadWriteNBT data = nbt.resolveOrCreateCompound(TERRA_COMPONENT_KEY + ".durability");
                addToCompound(data);
            });
        }
    }

    @Override
    public void updateLore() {

    }

    public static void clear(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt -> {
                nbt.removeKey(MINECRAFT_PREFIX + "unbreakable");
                nbt.resolveOrCreateCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY).removeKey("durability");
            });
        } else {
            NBT.modify(item, nbt -> {
                nbt.removeKey("Unbreakable");
                nbt.resolveOrCreateCompound(TERRA_COMPONENT_KEY).removeKey("durability");
            });
        }
    }

    public static void remove(ItemStack item) {
        clear(item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(damage, maxDamage, breakLoss);
    }

    @Override
    public String getComponentName() {
        return "terra_durability";
    }

    @Override
    public TerraBaseComponent updatePartial() {
        return new DurabilityComponent(null, this.maxDamage, this.breakLoss, this.state);
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

    @Override
    public boolean broken() {
        if (this.damage == null) return false;
        return this.damage == this.maxDamage;
    }

    @Override
    public String toString() {
        return BOLD + YELLOW + "terra_durability:" + RESET + "\n" +
                "    " + AQUA + "damage:" + WHITE + (damage != null ? damage : "null") + RESET + "\n" +
                "    " + AQUA + "max_damage:" + WHITE + maxDamage + RESET + "\n" +
                "    " + AQUA + "break_loss:" + WHITE + (breakLoss != null ? breakLoss : "null") + RESET + "\n" +
                "    " + AQUA + "broken:" + WHITE + broken() + RESET + "\n" +
                "    " + AQUA + "state:" + WHITE + state + RESET;
    }

    private void addToCompound(ReadWriteNBT compound) {
        compound.setInteger("max_damage", maxDamage);
        compound.setByte("state", state.toNbtByte());
        if (damage != null) compound.setInteger("damage", damage);
        if (breakLoss != null) compound.setBoolean("break_loss", breakLoss);
    }
}
