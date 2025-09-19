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
import io.github.tanice.terraCraft.core.config.ConfigManager;
import io.github.tanice.terraCraft.core.util.expression.TerraExpression;
import io.github.tanice.terraCraft.core.util.logger.TerraCraftLogger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Objects;

import static io.github.tanice.terraCraft.api.command.TerraCommand.*;

public class DurabilityComponent extends AbstractItemComponent implements TerraDurabilityComponent {
    @Nullable
    private Integer damage;
    private int maxDamage;
    @Nullable
    private Boolean breakLoss;
    @Nullable
    private String damageExpr; /* 每次使用消耗的耐久 */

    public DurabilityComponent(@Nullable Integer damage, int maxDamage, @Nullable Boolean breakLoss, @Nullable String damageExpr, boolean updatable) {
        super(updatable);
        this.damage = damage;
        this.maxDamage = maxDamage;
        this.breakLoss = breakLoss;
        this.damageExpr = damageExpr;
        registerExpression();
    }

    public DurabilityComponent(@Nullable Integer damage, int maxDamage, @Nullable Boolean breakLoss, @Nullable String damageExpr, ComponentState state) {
        super(state);
        this.damage = damage;
        this.maxDamage = maxDamage;
        this.breakLoss = breakLoss;
        this.damageExpr = damageExpr;
        registerExpression();
    }

    public DurabilityComponent(ConfigurationSection cfg) {
        super(cfg.getBoolean("updatable", true));
        this.damage = cfg.isSet("damage") ? cfg.getInt("damage") : null;
        this.maxDamage = cfg.getInt("max_damage", 1);
        this.breakLoss = cfg.isSet("break_loss") ? cfg.getBoolean("break_loss") : null;
        this.damageExpr = cfg.getString("damage_per_use_expr");
        registerExpression();
    }

    @Nullable
    public static DurabilityComponent from(ItemStack item) {
        if (item == null || item.isEmpty()) return null;
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            return NBT.getComponents(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".durability");
                if (data == null) return null;
                return new DurabilityComponent(data.getInteger("damage"), data.getInteger("max_damage"), data.getBoolean("break_loss"), data.getString("expr"), new ComponentState(data.getByte("state")));
            });
        } else {
            return NBT.get(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(TERRA_COMPONENT_KEY + ".durability");
                if (data == null) return null;
                return new DurabilityComponent(data.getInteger("damage"), data.getInteger("max_damage"), data.getBoolean("break_loss"), data.getString("expr"), new ComponentState(data.getByte("state")));
            });
        }
    }


    @Override
    protected void doCover(ItemStack item) {
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
    protected void updateLore() {

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
        return new DurabilityComponent(null, this.maxDamage, this.breakLoss, this.damageExpr, this.state);
    }

    @Override
    public int getDamage() {
        return this.damage == null ? 0 : this.damage;
    }

    @Override
    public void setDamage(int damage) {
        this.damage = Math.min(damage, this.maxDamage);
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
    @Nullable
    public String getDamageExpr() {
        return this.damageExpr;
    }

    @Override
    public void setDamageExpr(@Nullable String damageExpr) {
        // 不删除原本的公式
        this.damageExpr = damageExpr;
        registerExpression();
    }

    @Override
    public int getDamageForUse(double damage) {
        if (damageExpr == null || damageExpr.isBlank()) return -1;
        try {
            double v = (double) TerraExpression.calculate(damageExpr, new Object[]{this.damage, this.maxDamage, damage});
            if (ConfigManager.isDebug())
                TerraCraftLogger.debug(TerraCraftLogger.DebugLevel.EXPRESSION, "expression: " + damageExpr + ", damage_per_use result=" + v);
            return (int) v;
        } catch (Exception e) {
            TerraCraftLogger.error("Error when calculating damage_per_use in terra durability component: " + damageExpr + ". \n" + e.getMessage());
            return -1;
        }
    }

    @Override
    public boolean broken() {
        if (this.damage == null) return false;
        return this.damage >= this.maxDamage;
    }

    @Override
    public String toString() {
        return BOLD + YELLOW + "terra_durability:" + RESET + "\n" +
                "    " + AQUA + "damage:" + WHITE + (damage != null ? damage : "null") + "\n" +
                "    " + AQUA + "max_damage:" + WHITE + maxDamage + "\n" +
                "    " + AQUA + "damage_per_use_expr:" + WHITE + damageExpr + "\n" +
                "    " + AQUA + "break_loss:" + WHITE + isBreakLoss() + "\n" +
                "    " + AQUA + "broken:" + WHITE + broken() + "\n" +
                "    " + AQUA + "state:" + WHITE + state + RESET;
    }

    private void addToCompound(ReadWriteNBT compound) {
        compound.setInteger("max_damage", maxDamage);
        compound.setByte("state", state.toNbtByte());
        if (damage != null) compound.setInteger("damage", damage);
        if (breakLoss != null) compound.setBoolean("break_loss", breakLoss);
        if (damageExpr != null) compound.setString("expr", damageExpr);
    }

    private void registerExpression() {
        if (damageExpr == null || damageExpr.isBlank()) return;
        try {
            TerraExpression.register(
                    damageExpr,
                    damageExpr,
                    double.class,
                    // cur_damage 当前损伤值  max_damage 最大损伤值  damage 受到的伤害
                    new String[]{"damage", "max_damage", "harm"},
                    new Class[]{int.class, int.class, double.class}
            );
        } catch (Exception e) {
            TerraCraftLogger.error("Failed to register damage_per_use_expr expression: " + damageExpr + "\n" + e.getMessage());
        }
    }
}
