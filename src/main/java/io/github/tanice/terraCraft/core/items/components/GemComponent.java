package io.github.tanice.terraCraft.core.items.components;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.TerraGemComponent;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public class GemComponent implements TerraGemComponent {
    @Nullable
    private Float inlaySuccessChance;
    @Nullable
    private Float dismantleSuccessChance;
    @Nullable
    private Boolean inlayFailLoss;
    @Nullable
    private Boolean dismantleFailLoss;

    public GemComponent(@Nullable Float inlaySuccessChance, @Nullable Boolean inlayFailLoss, @Nullable Float dismantleSuccessChance, @Nullable Boolean dismantleFailLoss) {
        this.inlaySuccessChance = inlaySuccessChance;
        this.inlayFailLoss = inlayFailLoss;
        this.dismantleSuccessChance = dismantleSuccessChance;
        this.dismantleFailLoss = dismantleFailLoss;
    }

    @Nullable
    public static GemComponent from(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            return NBT.getComponents(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(COMPONENT_KEY + "." + MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".gem");
                if (data == null) return null;
                return new GemComponent(data.getFloat("inlay_chance"), data.getBoolean("inlay_loss"), data.getFloat("dismantle_chance"), data.getBoolean("dismantle_loss"));
            });
        } else {
            return NBT.get(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(TAG_KEY + "." + TERRA_COMPONENT_KEY + ".gem");
                if (data == null) return null;
                return new GemComponent(data.getFloat("inlay_chance"), data.getBoolean("inlay_loss"), data.getFloat("dismantle_chance"), data.getBoolean("dismantle_loss"));
            });
        }
    }

    @Override
    public void apply(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                ReadWriteNBT data = nbt.resolveOrCreateCompound(COMPONENT_KEY + "." + MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + "." + "gem");
                if (inlaySuccessChance != null) data.setFloat("inlay_chance", inlaySuccessChance);
                if (inlayFailLoss != null) data.setBoolean("inlay_loss", inlayFailLoss);
                if (dismantleSuccessChance != null) data.setFloat("dismantle_chance", dismantleSuccessChance);
                if (dismantleFailLoss != null) data.setBoolean("dismantle_loss", dismantleFailLoss);
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt -> {
                ReadWriteNBT data = nbt.resolveOrCreateCompound(TAG_KEY + "." + TERRA_COMPONENT_KEY + ".gem");
                if (inlaySuccessChance != null) data.setFloat("inlay_chance", inlaySuccessChance);
                if (inlayFailLoss != null) data.setBoolean("inlay_loss", inlayFailLoss);
                if (dismantleSuccessChance != null) data.setFloat("dismantle_chance", dismantleSuccessChance);
                if (dismantleFailLoss != null) data.setBoolean("dismantle_loss", dismantleFailLoss);
            });
        }
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
}
