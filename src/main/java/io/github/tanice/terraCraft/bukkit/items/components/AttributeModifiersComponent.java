package io.github.tanice.terraCraft.bukkit.items.components;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;

import io.github.tanice.terraCraft.api.items.components.vanilla.TerraAttributeModifiersComponent;
import io.github.tanice.terraCraft.bukkit.utils.MiniMessageUtil;
import io.github.tanice.terraCraft.bukkit.utils.adapter.BukkitAttribute;
import io.github.tanice.terraCraft.core.utils.slots.TerraEquipmentSlot;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import io.github.tanice.terraCraft.core.utils.namespace.TerraNamespaceKey;
import net.kyori.adventure.text.Component;

import javax.annotation.Nullable;
import java.util.UUID;

import static io.github.tanice.terraCraft.core.utils.EnumUtil.safeValueOf;

/**
 * 原版属性
 */
public class AttributeModifiersComponent implements TerraAttributeModifiersComponent {
    private final double amount;
    private final TerraNamespaceKey id;
    private final BukkitAttribute attributeType;
    private final Operation op;
    @Nullable
    private final TerraEquipmentSlot slot;
    private final DisplayType displayType;
    private final Component extraValue;

    public AttributeModifiersComponent(String id, BukkitAttribute attribute, double amount, String op, @Nullable String slot, @Nullable DisplayType displayType, @Nullable Component extraValue) {
        this.id = new TerraNamespaceKey(id);
        this.attributeType = attribute;
        this.amount = amount;
        this.op = safeValueOf(Operation.class, op, Operation.ADD);
        this.slot = safeValueOf(TerraEquipmentSlot.class, slot, null);
        this.displayType = displayType == null ? DisplayType.DEFAULT : displayType;
        this.extraValue = extraValue == null ? Component.empty() : extraValue;

    }

    @Override
    public void apply(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                ReadWriteNBT component = nbt.getOrCreateCompound(COMPONENT_KEY).getCompoundList(MINECRAFT_PREFIX + "attribute_modifiers").addCompound();
                component.setDouble("amount", amount);
                component.setString("type", attributeType.getBukkitAttribute().name());
                component.setString("operation", "Op" + op.getOperation());

                if (ServerVersion.isBefore(MinecraftVersions.v1_21_1)) {
                    component.setString("name", id.get());
                    component.getIntArrayList("uuid");
                }
                else component.setString("id", id.get());

                // 新版本处理display
                if (displayType != DisplayType.DEFAULT) {
                    if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_6)) {
                        ReadWriteNBT dComponent = component.getOrCreateCompound("display");
                        dComponent.setString("type", displayType.name().toLowerCase());
                        dComponent.setString("value", MiniMessageUtil.toNBTJson(extraValue));
                    } else TerraCraftLogger.warning("display in attribute modifiers component is only supported in Minecraft 1.21.5 or newer versions");
                }
                if (slot != null && slot != TerraEquipmentSlot.ANY) component.setString("slot", slot.getStandardName());
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt -> {
                ReadWriteNBT component = nbt.getOrCreateCompound(TAG_KEY).getCompoundList("AttributeModifiers").addCompound();
                component.setDouble("Amount", amount);
                component.setString("AttributeName", attributeType.getBukkitAttribute().name());
                component.setString("Name", id.get());
                component.setString("Operation", "Op" + op.getOperation());
                component.setUUID("UUID", UUID.randomUUID());
                if (slot != null && slot != TerraEquipmentSlot.ANY) component.setString("Slot", slot.getStandardName());
            });
        }
    }
}
