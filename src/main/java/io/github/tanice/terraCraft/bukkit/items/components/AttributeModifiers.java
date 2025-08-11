package io.github.tanice.terraCraft.bukkit.items.components;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.TerraAttributeModifiers;
import io.github.tanice.terraCraft.bukkit.utils.adapter.BukkitAttribute;
import io.github.tanice.terraCraft.bukkit.utils.slots.TerraEquipmentSlot;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import io.github.tanice.terraCraft.core.utils.namespace.TerraNamespaceKey;
import net.kyori.adventure.text.TextComponent;

import java.util.UUID;

public class AttributeModifiers implements TerraAttributeModifiers {

    private final TerraNamespaceKey namespace;

    private final BukkitAttribute attributeType;
    private final double amount;
    private final Operation op;
    private final TerraEquipmentSlot slot;

    private final DisplayType displayType;
    private final TextComponent extraValue;

    public AttributeModifiers(String namespaceKey, BukkitAttribute attribute, double amount, Operation op) {
        this(namespaceKey, attribute, amount, op, TerraEquipmentSlot.ANY, DisplayType.DEFAULT, null);
    }

    public AttributeModifiers(String namespaceKey, BukkitAttribute attribute, double amount, Operation op, TerraEquipmentSlot slot) {
        this(namespaceKey, attribute, amount, op, slot, DisplayType.DEFAULT, null);
    }

    public AttributeModifiers(String namespaceKey, BukkitAttribute attribute, double amount, Operation op, TerraEquipmentSlot slot, DisplayType displayType, TextComponent extraValue) {
        this.namespace = new TerraNamespaceKey(namespaceKey);
        this.attributeType = attribute;
        this.amount = amount;
        this.op = op;
        this.slot = slot;
        this.displayType = displayType;
        this.extraValue = extraValue;
    }

    @Override
    public void apply(TerraBaseItem item) {
        boolean isOldVersion = ServerVersion.isBeforeOrEq(MinecraftVersions.v1_20_4);
        boolean isAnySlot = slot == TerraEquipmentSlot.ANY;

        if (isAnySlot) {
            modifyNbtComponent(item, isOldVersion, null);
        } else {
            for (String slotName : slot.getStandardEquippableName()) {
                modifyNbtComponent(item, isOldVersion, slotName);
            }
        }
    }

    /**
     * 提取公共NBT组件修改逻辑
     * @param item 物品实例
     * @param isOldVersion 是否为1.20.4及之前版本
     * @param slotName 槽位名称（ANY时为null）
     */
    private void modifyNbtComponent(TerraBaseItem item, boolean isOldVersion, String slotName) {
        NBT.modifyComponents(item.getBukkitItem(), nbt -> {
            ReadWriteNBT component;

            if (isOldVersion) {
                component = nbt.getCompoundList("AttributeModifiers").addCompound();
                component.setDouble("Amount", amount);
                component.setString("AttributeName", attributeType.getBukkitAttribute().name());
                component.setString("Name", namespace.get());
                component.setString("Operation", "Op" + op.getOperation());
                component.setUUID("UUID", UUID.randomUUID());
            } else {
                component = nbt.getOrCreateCompound(COMPONENT_KEY).getCompoundList(MINECRAFT_PREFIX + "attribute_modifiers").addCompound();
                component.setDouble("amount", amount);
                component.setString("type", attributeType.getBukkitAttribute().name());
                component.setString("id", namespace.get());
                component.setString("operation", "Op" + op.getOperation());

                // 新版本处理display
                if (displayType != DisplayType.DEFAULT) {
                    ReadWriteNBT dComponent = component.getOrCreateCompound("display");
                    dComponent.setString("type", displayType.name().toLowerCase());
                    dComponent.setString("value", extraValue.toString());
                }
            }

            // 设置槽位
            if (slotName != null) {
                component.setString(isOldVersion ? "Slot" : "slot", slotName);
            }
        });
    }
}
