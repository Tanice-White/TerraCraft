package io.github.tanice.terraCraft.bukkit.item.component;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import io.github.tanice.terraCraft.api.item.component.ComponentState;
import io.github.tanice.terraCraft.api.item.component.TerraGemComponent;
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

public class GemComponent extends AbstractItemComponent implements TerraGemComponent {
    @Nullable
    private String inlaySuccessExpr;
    @Nullable
    private String dismantleSuccessExpr;
    @Nullable
    private Boolean inlayFailLoss;
    @Nullable
    private Boolean dismantleFailLoss;

    public GemComponent(@Nullable String inlaySuccessExpr, @Nullable Boolean inlayFailLoss, @Nullable String dismantleSuccessExpr, @Nullable Boolean dismantleFailLoss, boolean updatable) {
        super(updatable);
        this.inlaySuccessExpr = inlaySuccessExpr;
        this.inlayFailLoss = inlayFailLoss;
        this.dismantleSuccessExpr = dismantleSuccessExpr;
        this.dismantleFailLoss = dismantleFailLoss;
        registerExpression();
    }

    public GemComponent(@Nullable String inlaySuccessExpr, @Nullable Boolean inlayFailLoss, @Nullable String dismantleSuccessExpr, @Nullable Boolean dismantleFailLoss, ComponentState state) {
        super(state);
        this.inlaySuccessExpr = inlaySuccessExpr;
        this.inlayFailLoss = inlayFailLoss;
        this.dismantleSuccessExpr = dismantleSuccessExpr;
        this.dismantleFailLoss = dismantleFailLoss;
        registerExpression();
    }

    public GemComponent(ConfigurationSection cfg) {
        super(cfg.getBoolean("updatable", true));
        this.inlaySuccessExpr = cfg.getString("inlay_chance_expr");
        this.inlayFailLoss = cfg.isSet("inlay_fail_loss") ? cfg.getBoolean("inlay_fail_loss") : null;
        this.dismantleSuccessExpr = cfg.getString("dismantle_chance_expr");
        this.dismantleFailLoss = cfg.isSet("dismantle_fail_loss") ? cfg.getBoolean("dismantle_fail_loss") : null;
        registerExpression();
    }

    @Nullable
    public static GemComponent from(ItemStack item) {
        if (item == null || item.isEmpty()) return null;
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            return NBT.getComponents(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".gem");
                if (data == null) return null;
                return fromNBT(data);
            });
        } else {
            return NBT.get(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(TERRA_COMPONENT_KEY + ".gem");
                if (data == null) return null;
                return fromNBT(data);
            });
        }
    }

    @Override
    protected void doCover(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt -> {
                ReadWriteNBT data = nbt.resolveOrCreateCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".gem");
                addToCompound(data);
            });
        } else {
            NBT.modify(item, nbt -> {
                ReadWriteNBT data = nbt.resolveOrCreateCompound(TERRA_COMPONENT_KEY + ".gem");
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
                nbt.resolveOrCreateCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY).removeKey("gem");
            });
        } else {
            NBT.modify(item, nbt -> {
                nbt.resolveOrCreateCompound(TERRA_COMPONENT_KEY).removeKey("gem");
            });
        }
    }

    public static void remove(ItemStack item) {
        clear(item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inlaySuccessExpr, inlayFailLoss, dismantleSuccessExpr, dismantleFailLoss);
    }

    @Override
    public float getInlaySuccessChance(int gemNum, int limit) {
        if (inlaySuccessExpr == null || inlaySuccessExpr.isBlank()) return 1f;
        try {
            double v = (double) TerraExpression.calculate(inlaySuccessExpr, new Object[]{gemNum, limit});
            if (ConfigManager.isDebug())
                TerraCraftLogger.debug(TerraCraftLogger.DebugLevel.EXPRESSION, "expression: " + inlaySuccessExpr + ", chance result=" + v);
            return (float) v;
        } catch (Exception e) {
            TerraCraftLogger.error("Error when calculating chance in gem component when inserting: " + inlaySuccessExpr + ". \n" + e.getMessage());
            return 1f;
        }
    }

    @Override
    public void setInlaySuccessExpr(String inlaySuccessExpr) {
        this.inlaySuccessExpr = inlaySuccessExpr;
        registerExpression1();
    }

    @Override
    public float getDismantleSuccessChance(int gemNum, int limit) {
        if (dismantleSuccessExpr == null || dismantleSuccessExpr.isBlank()) return 1f;
        try {
            double v = (double) TerraExpression.calculate(dismantleSuccessExpr, new Object[]{gemNum, limit});
            if (ConfigManager.isDebug())
                TerraCraftLogger.debug(TerraCraftLogger.DebugLevel.EXPRESSION, "expression: " + dismantleSuccessExpr + ", chance result=" + v);
            return (float) v;
        } catch (Exception e) {
            TerraCraftLogger.error("Error when calculating chance in gem component when dismantling: " + dismantleSuccessExpr + ". \n" + e.getMessage());
            return 1f;
        }
    }

    @Override
    public void setDismantleSuccessExpr(String dismantleSuccessExpr) {
        this.dismantleSuccessExpr = dismantleSuccessExpr;
        registerExpression2();
    }

    @Override
    public boolean isInlayFailLoss() {
        return inlayFailLoss != null ? inlayFailLoss : false;
    }

    @Override
    public void setInlayFailLoss(boolean loss) {
        this.inlayFailLoss = loss;
    }

    @Override
    public boolean isDismantleFailLoss() {
        return dismantleFailLoss != null ? dismantleFailLoss : false;
    }

    @Override
    public void setDismantleFailLoss(boolean loss) {
        this.dismantleFailLoss = loss;
    }

    @Override
    public String getComponentName() {
        return "gem";
    }

    @Override
    public String toString() {
        return BOLD + YELLOW + "gem:" + "\n" +
                "    " + AQUA + "inlay_chance_expr:" +
                WHITE + (inlaySuccessExpr != null ? inlaySuccessExpr : "1") + "\n" +
                "    " + AQUA + "inlay_fail_loss:" +
                WHITE + (inlayFailLoss != null ? inlayFailLoss : "false") + "\n" +
                "    " + AQUA + "dismantle_chance_expr:" +
                WHITE + (dismantleSuccessExpr != null ? dismantleSuccessExpr : "1") + "\n" +
                "    " + AQUA + "dismantle_fail_loss:" +
                WHITE + (dismantleFailLoss != null ? dismantleFailLoss : "false") + "\n" +
                "    " + AQUA + "state:" + WHITE + state + RESET;
    }

    private void addToCompound(ReadWriteNBT compound) {
        compound.setByte("state", state.toNbtByte());
        if (inlaySuccessExpr != null) compound.setString("inlay_chance_expr", inlaySuccessExpr);
        if (inlayFailLoss != null) compound.setBoolean("inlay_loss", inlayFailLoss);
        if (dismantleSuccessExpr != null) compound.setString("dismantle_chance_expr", dismantleSuccessExpr);
        if (dismantleFailLoss != null) compound.setBoolean("dismantle_loss", dismantleFailLoss);
    }

    private static GemComponent fromNBT(ReadableNBT nbt) {
        return new GemComponent(
                nbt.getString("inlay_chance_expr"),
                nbt.getBoolean("inlay_loss"),
                nbt.getString("dismantle_chance_expr"),
                nbt.getBoolean("dismantle_loss"),
                new ComponentState(nbt.getByte("state"))
        );
    }

    private void registerExpression() {
        registerExpression1();
        registerExpression2();
    }

    private void registerExpression1() {
        if (inlaySuccessExpr == null || inlaySuccessExpr.isBlank()) return;
        try {
            TerraExpression.register(
                    inlaySuccessExpr,
                    inlaySuccessExpr,
                    double.class,
                    // gem_num 当前宝石数量  limit 槽位数量
                    // 必须用double，否则计算过程全是int就会变成0
                    new String[]{"gem_num", "limit"},
                    new Class[]{double.class, double.class}
            );
        } catch (Exception e) {
            TerraCraftLogger.error("Failed to register inlay chance expression: " + inlaySuccessExpr + "\n" + e.getMessage());
        }

    }

    private void registerExpression2() {
        if (dismantleSuccessExpr == null || dismantleSuccessExpr.isBlank()) return;
        try {
            TerraExpression.register(
                    dismantleSuccessExpr,
                    dismantleSuccessExpr,
                    double.class,
                    new String[]{"gem_num", "limit"},
                    new Class[]{int.class, int.class}
            );
        } catch (Exception e) {
            TerraCraftLogger.error("Failed to register dismantle chance expression: " + dismantleSuccessExpr + "\n" + e.getMessage());
        }

    }
}
