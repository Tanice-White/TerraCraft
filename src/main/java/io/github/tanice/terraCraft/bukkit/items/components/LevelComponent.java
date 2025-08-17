package io.github.tanice.terraCraft.bukkit.items.components;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.ComponentState;
import io.github.tanice.terraCraft.api.items.components.TerraBaseComponent;
import io.github.tanice.terraCraft.api.items.components.TerraLevelComponent;
import io.github.tanice.terraCraft.api.items.components.AbstractItemComponent;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * 等级模板扩展
 */
public class LevelComponent extends AbstractItemComponent implements TerraLevelComponent {
    @Nullable
    private Integer level;
    @Nullable
    private String levelTemplate;

    public LevelComponent(@Nullable Integer level, @Nullable String levelTemplate, boolean updatable) {
        super(updatable);
        this.level = level;
        this.levelTemplate = levelTemplate;
    }

    public LevelComponent(@Nullable Integer level, @Nullable String levelTemplate, ComponentState state) {
        super(state);
        this.level = level;
        this.levelTemplate = levelTemplate;
    }

    public LevelComponent(ConfigurationSection cfg) {
        super(cfg.getBoolean("updatable", true));
        this.level = cfg.isSet("lvl") ? cfg.getInt("lvl") : null;
        this.levelTemplate = cfg.getString("lvlTemplate");
    }

    @Nullable
    public static LevelComponent from(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            return NBT.getComponents(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(COMPONENT_KEY + "." + MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".level");
                if (data == null) return null;
                return fromNBT(data);
            });
        } else {
            return NBT.get(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(TAG_KEY + "." + TERRA_COMPONENT_KEY + ".level");
                if (data == null) return null;
                return fromNBT(data);
            });
        }
    }

    @Override
    public void apply(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                ReadWriteNBT data = nbt.resolveOrCreateCompound(COMPONENT_KEY + "." + MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".level");
                addToCompound(data);
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt -> {
                ReadWriteNBT data = nbt.resolveOrCreateCompound(TAG_KEY + "." + TERRA_COMPONENT_KEY + ".level");
                addToCompound(data);
            });
        }
    }

    public static void clear(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.resolveOrCreateCompound(COMPONENT_KEY + "." + MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY).removeKey("level");
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt -> {
                nbt.resolveOrCreateCompound(TAG_KEY + "." + TERRA_COMPONENT_KEY).removeKey("level");
            });
        }
    }

    public static void remove(TerraBaseItem item) {
        clear(item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(level, levelTemplate);
    }

    @Override
    public void updatePartialFrom(TerraBaseComponent old) {
        this.level = ((LevelComponent) old).level;
    }

    @Override
    public int getLevel() {
        return this.level == null ? 0 : this.level;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public @Nullable String getTemplate() {
        return this.levelTemplate;
    }

    @Override
    public void setTemplate(String template) {
        this.levelTemplate = template;
    }

    private void addToCompound(ReadWriteNBT compound) {
        if (level != null) compound.setInteger("lvl", level);
        if (levelTemplate != null) compound.setString("template", levelTemplate);
        compound.setByte("state", state.toNbtByte());
    }

    private static LevelComponent fromNBT(ReadableNBT nbt) {
        return new LevelComponent(nbt.getInteger("lvl"), nbt.getString("template"), new ComponentState(nbt.getByte("state")));
    }
}
