package io.github.tanice.terraCraft.bukkit.item.component;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import io.github.tanice.terraCraft.api.item.component.ComponentState;
import io.github.tanice.terraCraft.api.item.component.TerraGemComponent;
import io.github.tanice.terraCraft.api.item.component.AbstractItemComponent;
import io.github.tanice.terraCraft.bukkit.util.version.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.util.version.ServerVersion;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Objects;

import static io.github.tanice.terraCraft.api.command.TerraCommand.*;

public class GemComponent extends AbstractItemComponent implements TerraGemComponent {
    @Nullable
    private Float inlaySuccessChance;
    @Nullable
    private Float dismantleSuccessChance;
    @Nullable
    private Boolean inlayFailLoss;
    @Nullable
    private Boolean dismantleFailLoss;

    public GemComponent(@Nullable Float inlaySuccessChance, @Nullable Boolean inlayFailLoss, @Nullable Float dismantleSuccessChance, @Nullable Boolean dismantleFailLoss, boolean updatable) {
        super(updatable);
        this.inlaySuccessChance = inlaySuccessChance;
        this.inlayFailLoss = inlayFailLoss;
        this.dismantleSuccessChance = dismantleSuccessChance;
        this.dismantleFailLoss = dismantleFailLoss;
    }

    public GemComponent(@Nullable Float inlaySuccessChance, @Nullable Boolean inlayFailLoss, @Nullable Float dismantleSuccessChance, @Nullable Boolean dismantleFailLoss, ComponentState state) {
        super(state);
        this.inlaySuccessChance = inlaySuccessChance;
        this.inlayFailLoss = inlayFailLoss;
        this.dismantleSuccessChance = dismantleSuccessChance;
        this.dismantleFailLoss = dismantleFailLoss;
    }

    public GemComponent(ConfigurationSection cfg) {
        super(cfg.getBoolean("updatable", true));
        this.inlaySuccessChance = cfg.isSet("inlay_chance") ? (float) cfg.getDouble("inlay_chance") : null;
        this.inlayFailLoss = cfg.isSet("inlay_fail_loss") ? cfg.getBoolean("inlay_fail_loss") : null;
        this.dismantleSuccessChance = cfg.isSet("dismantle_chance") ? (float) cfg.getDouble("dismantle_chance") : null;
        this.dismantleFailLoss = cfg.isSet("dismantle_fail_loss") ? cfg.getBoolean("dismantle_fail_loss") : null;
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
    public void doCover(ItemStack item) {
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
    public void updateLore() {

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
        return Objects.hash(inlaySuccessChance, inlayFailLoss, dismantleSuccessChance, dismantleFailLoss);
    }

    @Override
    public float getInlaySuccessChance() {
        return inlaySuccessChance != null ? inlaySuccessChance : 1f;
    }

    @Override
    public void setInlaySuccessChance(float chance) {
        this.inlaySuccessChance = chance;
    }

    @Override
    public float getDismantleSuccessChance() {
        return dismantleSuccessChance != null ? dismantleSuccessChance : 1f;
    }

    @Override
    public void setDismantleSuccessChance(float chance) {
        this.dismantleSuccessChance = chance;
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
                "    " + AQUA + "inlay_chance:" +
                WHITE + (inlaySuccessChance != null ? inlaySuccessChance : "1(default)") + "\n" +
                "    " + AQUA + "inlay_fail_loss:" +
                WHITE + (inlayFailLoss != null ? inlayFailLoss : "1(default)") + "\n" +
                "    " + AQUA + "dismantle_chance:" +
                WHITE + (dismantleSuccessChance != null ? dismantleSuccessChance : "1(default)") + "\n" +
                "    " + AQUA + "dismantle_fail_loss:" +
                WHITE + (dismantleFailLoss != null ? dismantleFailLoss : "1(default)") + "\n" +
                "    " + AQUA + "state:" + WHITE + state + RESET;
    }

    private void addToCompound(ReadWriteNBT compound) {
        compound.setByte("state", state.toNbtByte());
        if (inlaySuccessChance != null) compound.setFloat("inlay_chance", inlaySuccessChance);
        if (inlayFailLoss != null) compound.setBoolean("inlay_loss", inlayFailLoss);
        if (dismantleSuccessChance != null) compound.setFloat("dismantle_chance", dismantleSuccessChance);
        if (dismantleFailLoss != null) compound.setBoolean("dismantle_loss", dismantleFailLoss);
    }

    private static GemComponent fromNBT(ReadableNBT nbt) {
        return new GemComponent(
                nbt.getFloat("inlay_chance"),
                nbt.getBoolean("inlay_loss"),
                nbt.getFloat("dismantle_chance"),
                nbt.getBoolean("dismantle_loss"),
                new ComponentState(nbt.getByte("state"))
        );
    }
}
