package io.github.tanice.terraCraft.bukkit.item.component.vanilla;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBTCompoundList;

import io.github.tanice.terraCraft.api.item.component.vanilla.TerraAttributeModifiersComponent;
import io.github.tanice.terraCraft.bukkit.util.MiniMessageUtil;
import io.github.tanice.terraCraft.bukkit.util.adapter.BukkitAttribute;
import io.github.tanice.terraCraft.core.util.slot.TerraEquipmentSlot;
import io.github.tanice.terraCraft.bukkit.util.version.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.util.version.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import io.github.tanice.terraCraft.core.util.namespace.TerraNamespaceKey;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static io.github.tanice.terraCraft.core.util.EnumUtil.safeValueOf;

/**
 * 原版属性
 */
public class AttributeModifiersComponent implements TerraAttributeModifiersComponent {

    private final List<AttributeModifierComponent> modifiers;

    public AttributeModifiersComponent() {
        modifiers = new ArrayList<>();
    }

    public AttributeModifiersComponent(ConfigurationSection cfg) {
        modifiers = new ArrayList<>();

        ConfigurationSection sub;
        for (String key : cfg.getKeys(false)) {
            if (key.isBlank()) {
                TerraCraftLogger.warning("Attribute modifier cannot have a blank key");
                continue;
            }
            sub = cfg.getConfigurationSection(key);
            if (sub == null) {
                TerraCraftLogger.warning("Invalid Attribute format under: " + key);
                return;
            }
            modifiers.add(new AttributeModifierComponent(
                    key,
                    sub.getString("attr"),
                    sub.isSet("amount") ? sub.getDouble("amount") : 1,
                    sub.getString("op"),
                    sub.getString("slot"),
                    sub.getString("display_type"),
                    sub.getString("value")
            ));
        }
    }

    @Override
    public void addAttributeModifier(String id, String attribute, double amount, String op, @Nullable String slot, @Nullable String displayType, @Nullable String extraValue) {
        modifiers.add(new AttributeModifierComponent(id, attribute, amount, op, slot, displayType, extraValue));
    }

    @Override
    public void cover(ItemStack item) {
        if (modifiers.isEmpty()) return;
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt -> {
                ReadWriteNBTCompoundList compoundList = nbt.getCompoundList(MINECRAFT_PREFIX + "attribute_modifiers");
                /* 覆盖 */
                compoundList.clear();
                ReadWriteNBT component;
                for (AttributeModifierComponent modifier : modifiers) {
                    component = compoundList.addCompound();
                    component.setDouble("amount", modifier.amount);
                    component.setString("type", modifier.attributeType.getAttributeKey().asString());
                    component.setString("operation", modifier.op.name().toLowerCase());
                    if (ServerVersion.isBefore(MinecraftVersions.v1_21_1)) {
                        component.setString("name", modifier.id.get());
                        component.getIntArrayList("uuid");
                    }
                    else component.setString("id", modifier.id.get());
                    // 新版本处理display
                    if (modifier.displayType != DisplayType.DEFAULT) {
                        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_6)) {
                            ReadWriteNBT dComponent = component.getOrCreateCompound("display");
                            dComponent.setString("type", modifier.displayType.name().toLowerCase());
                            dComponent.setString("value", MiniMessageUtil.toNBTJson(modifier.extraValue));
                        } else TerraCraftLogger.warning("display in attribute modifiers component is only supported in Minecraft 1.21.5 or newer versions");
                    }
                    if (modifier.slot != null && modifier.slot != TerraEquipmentSlot.ANY) component.setString("slot", modifier.slot.getStandardName());
                }
            });
        } else {
            NBT.modify(item, nbt -> {
                ReadWriteNBTCompoundList compoundList = nbt.getCompoundList("AttributeModifiers");
                /* 覆盖 */
                compoundList.clear();
                ReadWriteNBT component;
                for (AttributeModifierComponent modifier : modifiers) {
                    component = compoundList.addCompound();
                    component.setDouble("Amount", modifier.amount);
                    component.setString("AttributeName", modifier.attributeType.getBukkitAttribute().toString());
                    component.setString("Name", modifier.id.get());
                    component.setString("Operation", modifier.op.name().toLowerCase());
                    component.setUUID("UUID", UUID.randomUUID());
                    if (modifier.slot != null && modifier.slot != TerraEquipmentSlot.ANY) component.setString("Slot", modifier.slot.getStandardName());
                }
            });
        }
    }

    @Override
    public String getComponentName() {
        return "attribute";
    }

    @Override
    public int hashCode() {
        return Objects.hash(modifiers);
    }

    public static void clear(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt -> {
                nbt.removeKey(MINECRAFT_PREFIX + "attribute_modifiers");
            });
        } else {
            NBT.modify(item, nbt -> {
                nbt.removeKey("AttributeModifiers");
            });
        }
    }

    public static void remove(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt -> {
                nbt.removeKey(MINECRAFT_PREFIX + "attribute_modifiers");
                nbt.getOrCreateCompound("!" + MINECRAFT_PREFIX + "attribute_modifiers");
            });
        } else {
            NBT.modify(item, nbt -> {
                nbt.removeKey("AttributeModifiers");
            });
        }
    }

    private static class AttributeModifierComponent {
        private final double amount;
        private final TerraNamespaceKey id;
        private final BukkitAttribute attributeType;
        private final Operation op;
        @Nullable
        private final TerraEquipmentSlot slot;
        private final DisplayType displayType;
        private final Component extraValue;

        public AttributeModifierComponent(String id, String attribute, double amount, String op, @Nullable String slot, @Nullable String displayType, @Nullable String extraValue) {
            this.id = TerraNamespaceKey.from(id);
            this.attributeType = BukkitAttribute.get(attribute);
            this.amount = amount;
            this.op = safeValueOf(Operation.class, op, Operation.ADD_VALUE);
            this.slot = safeValueOf(TerraEquipmentSlot.class, slot, null);
            this.displayType = safeValueOf(DisplayType.class, displayType, DisplayType.DEFAULT);
            this.extraValue = extraValue == null ? Component.empty() : MiniMessageUtil.serialize(extraValue);
        }

        @Override
        public int hashCode() {
            return Objects.hash(amount, id, attributeType, op, slot, displayType, extraValue);
        }
    }
}
