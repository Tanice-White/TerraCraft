package io.github.tanice.terraCraft.bukkit.items.components.vanilla;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.*;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.TerraBaseComponent;
import io.github.tanice.terraCraft.api.items.components.vanilla.TerraEnchantComponent;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import io.github.tanice.terraCraft.core.utils.namespace.TerraNamespaceKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Enchantable + Enchantments
 */
public class EnchantComponent implements TerraEnchantComponent {

    private final int enchantmentValue; /* Enchantable 1.21.2 加入 */
    @Nullable
    private final List<TerraNamespaceKey> enchantments;
    @Nullable
    private final List<Integer> levels;

    public EnchantComponent(@Nullable List<TerraNamespaceKey> enchantments, @Nullable List<Integer> levels) {
        this(0, enchantments, levels);
    }

    public EnchantComponent(int enchantmentValue, @Nullable List<TerraNamespaceKey> enchantments, @Nullable List<Integer> levels) {
        this.enchantmentValue = enchantmentValue;
        this.enchantments = enchantments;
        this.levels = levels;
    }

    public EnchantComponent(ConfigurationSection cfg) {
        this.enchantmentValue = cfg.getInt("value");
        this.enchantments = new ArrayList<>();
        this.levels = new ArrayList<>();
        List<String> list = cfg.getStringList("enchants");
        if (list.isEmpty()) return;

        String[] v;
        for (String enchant : list) {
            v = enchant.split(" ");
            if (v.length != 2) {
                TerraCraftLogger.warning("Invalid enchantments config in " + cfg.getCurrentPath());
                continue;
            }
            TerraNamespaceKey e = TerraNamespaceKey.from(v[0]);
            if (e == null) {
                TerraCraftLogger.warning("Invalid enchantments config in: " + cfg.getCurrentPath());
                continue;
            }
            this.enchantments.add(e);
            this.levels.add(Integer.parseInt(v[1]));
        }
    }

    @Override
    public void apply(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt -> {
                if (enchantmentValue > 0) {
                    if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
                        nbt.getOrCreateCompound(MINECRAFT_PREFIX + "enchantable").setInteger("value", enchantmentValue);
                    } else TerraCraftLogger.warning("Enchantable component is only supported in Minecraft 1.21.2 or newer versions");
                }
                if (enchantments != null && levels != null && !enchantments.isEmpty() && !levels.isEmpty()) {
                    ReadWriteNBT subCompound = nbt.getOrCreateCompound(MINECRAFT_PREFIX + "enchantments");
                    for (int i = 0; i < enchantments.size(); i++) {
                        subCompound.setInteger(enchantments.get(i).get(), levels.get(i));
                    }
                }
            });
        } else {
            NBT.modify(item, nbt ->{
                if (enchantments != null && levels != null && !enchantments.isEmpty() && !levels.isEmpty()) {
                    ReadWriteNBTCompoundList compoundList = nbt.getCompoundList("Enchantments");
                    ReadWriteNBT compound;
                    for (int i = 0; i < enchantments.size(); i++) {
                        compound = compoundList.addCompound();
                        compound.setString("id", enchantments.get(i).get());
                        compound.setShort("lvl", levels.get(i).shortValue());
                    }
                }
            });
            if (enchantmentValue >= 0) TerraCraftLogger.warning("Enchantable component is only supported in Minecraft 1.21.2 or newer versions");
        }
    }

    @Override
    public String getComponentName() {
        return "enchant";
    }

    public static void clear(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.removeKey(MINECRAFT_PREFIX + "enchantable");
                nbt.removeKey(MINECRAFT_PREFIX + "enchantments");
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt ->{
                nbt.removeKey("Enchantments");
            });
        }
    }

    public static void remove(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.removeKey(MINECRAFT_PREFIX + "enchantable");
                nbt.getOrCreateCompound("!" + MINECRAFT_PREFIX + "enchantable");
                nbt.removeKey(MINECRAFT_PREFIX + "enchantments");
                nbt.getOrCreateCompound("!" + MINECRAFT_PREFIX + "enchantments");
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt ->{
                nbt.removeKey("Enchantments");
            });
        }
    }

    @Override
    public TerraBaseComponent updatePartial() {
        return new EnchantComponent(this.enchantmentValue, null, null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enchantments, levels, enchantmentValue);
    }
}
