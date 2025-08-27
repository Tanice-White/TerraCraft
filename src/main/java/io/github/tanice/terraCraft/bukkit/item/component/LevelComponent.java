package io.github.tanice.terraCraft.bukkit.item.component;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import io.github.tanice.terraCraft.api.item.TerraBaseItem;
import io.github.tanice.terraCraft.api.item.component.ComponentState;
import io.github.tanice.terraCraft.api.item.component.TerraBaseComponent;
import io.github.tanice.terraCraft.api.item.component.TerraLevelComponent;
import io.github.tanice.terraCraft.api.item.component.AbstractItemComponent;
import io.github.tanice.terraCraft.bukkit.util.version.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.util.version.ServerVersion;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Objects;

import static io.github.tanice.terraCraft.api.command.TerraCommand.*;

/**
 * 等级模板扩展
 */
public class LevelComponent extends AbstractItemComponent implements TerraLevelComponent {
    /** 这里的lvl就是物品升级成功次数, 模板最值无关 */
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
        this.levelTemplate = cfg.getString("template");
    }

    @Nullable
    public static LevelComponent from(ItemStack item) {
        if (item == null || item.isEmpty()) return null;
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            return NBT.getComponents(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".level");
                if (data == null) return null;
                return fromNBT(data);
            });
        } else {
            return NBT.get(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(TERRA_COMPONENT_KEY + ".level");
                if (data == null) return null;
                return fromNBT(data);
            });
        }
    }

    @Override
    public void doApply(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt -> {
                ReadWriteNBT data = nbt.resolveOrCreateCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".level");
                addToCompound(data);
            });
        } else {
            NBT.modify(item, nbt -> {
                ReadWriteNBT data = nbt.resolveOrCreateCompound(TERRA_COMPONENT_KEY + ".level");
                addToCompound(data);
            });
        }
    }

    @Override
    public void updateLore() {

    }

    public static void clear(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.resolveOrCreateCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY).removeKey("level");
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt -> {
                nbt.resolveOrCreateCompound(TERRA_COMPONENT_KEY).removeKey("level");
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
    public String getComponentName() {
        return "level";
    }

    @Override
    public TerraBaseComponent updatePartial() {
        return new LevelComponent(null, this.levelTemplate, this.state);
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

    @Override
    public String toString() {
        return BOLD + YELLOW + "level:" + "\n" +
                "    " + AQUA + "lvl:" + WHITE + (level != null ? level : 0) + RESET + "\n" +
                "    " + AQUA + "template:" + (levelTemplate != null ? WHITE + levelTemplate : GRAY + "null") + RESET + "\n" +
                "    " + AQUA + "state:" + WHITE + state + RESET;
    }
}
