package io.github.tanice.terraCraft.bukkit.item.component;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import io.github.tanice.terraCraft.api.item.TerraBaseItem;
import io.github.tanice.terraCraft.api.item.component.AbstractItemComponent;
import io.github.tanice.terraCraft.api.item.component.ComponentState;
import io.github.tanice.terraCraft.api.item.component.TerraMetaSlotComponent;
import io.github.tanice.terraCraft.bukkit.util.version.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.util.version.ServerVersion;
import io.github.tanice.terraCraft.core.util.slot.TerraEquipmentSlot;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

import static io.github.tanice.terraCraft.api.command.TerraCommand.*;
import static io.github.tanice.terraCraft.api.command.TerraCommand.RESET;

/**
 * 决定terra属性等组件是否生效
 */
public class SlotComponent extends AbstractItemComponent implements TerraMetaSlotComponent {

    private TerraEquipmentSlot slot;

    public SlotComponent(String slot, boolean updatable) {
        super(updatable);
        this.slot = TerraEquipmentSlot.of(slot);
    }

    public SlotComponent(String slot, ComponentState state) {
        super(state);
        this.slot = TerraEquipmentSlot.of(slot);
    }

    public SlotComponent(ConfigurationSection cfg) {
        super(cfg.getBoolean("updatable", true));
        this.slot = TerraEquipmentSlot.of(cfg.getString("content"));
    }

    @Nullable
    public static SlotComponent from(ItemStack item) {
        if (item == null || item.isEmpty()) return null;
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            return NBT.getComponents(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".slot");
                if (data == null) return null;
                return new SlotComponent(data.getString("content"), new ComponentState(data.getByte("state")));
            });
        } else {
            return NBT.get(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(TERRA_COMPONENT_KEY + ".slot");
                if (data == null) return null;
                return new SlotComponent(data.getString("content"), new ComponentState(data.getByte("state")));
            });
        }
    }

    @Override
    public void doApply(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt -> {
                ReadWriteNBT data = nbt.resolveOrCreateCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".slot");
                addToCompound(data);
            });
        } else {
            NBT.modify(item, nbt -> {
                ReadWriteNBT data = nbt.resolveOrCreateCompound(TERRA_COMPONENT_KEY + ".slot");
                addToCompound(data);
            });
        }
    }

    public void clear(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.resolveOrCreateCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY).removeKey("slot");
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt -> {
                nbt.resolveOrCreateCompound(TERRA_COMPONENT_KEY).removeKey("slot");
            });
        }
    }

    public void remove(TerraBaseItem item) {
        clear(item);
    }

    @Override
    public void updateLore() {

    }

    @Override
    public String getComponentName() {
        return "slot";
    }

    @Override
    public boolean activeUnder(TerraEquipmentSlot conditionSlot) {
        return slot.equals(conditionSlot);
    }

    @Override
    public TerraEquipmentSlot getSlot() {
        return slot;
    }

    @Override
    public void setSlot(TerraEquipmentSlot slot) {
        this.slot = slot;
    }

    @Override
    public String toString() {
        return BOLD + YELLOW + "slot:" + "\n" +
                "    " + AQUA + "content:" + slot.getStandardName().toLowerCase() + "\n" +
                "    " + AQUA + "state:" + WHITE + state + RESET;
    }

    private void addToCompound(ReadWriteNBT compound) {
        compound.setString("content", slot.getStandardName());
        compound.setByte("state", state.toNbtByte());
    }
}
