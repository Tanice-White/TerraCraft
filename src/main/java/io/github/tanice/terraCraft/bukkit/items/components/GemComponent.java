package io.github.tanice.terraCraft.bukkit.items.components;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.ComponentState;
import io.github.tanice.terraCraft.api.items.components.TerraGemComponent;
import io.github.tanice.terraCraft.api.items.components.AbstractItemComponent;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Objects;

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
    public void doApply(ItemStack item) {
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
    public void callEvent() {

    }

    @Override
    public void updateLore() {

    }

    public static void clear(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.resolveOrCreateCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY).removeKey("gem");
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt -> {
                nbt.resolveOrCreateCompound(TERRA_COMPONENT_KEY).removeKey("gem");
            });
        }
    }

    public static void remove(TerraBaseItem item) {
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
