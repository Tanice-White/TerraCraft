package io.github.tanice.terraCraft.core.items.components;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.TerraLevelComponent;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

/**
 * 等级模板扩展
 */
public class LevelComponent implements TerraLevelComponent {
    @Nullable
    private Integer level;
    @Nullable
    private String levelTemplate;

    public LevelComponent(@Nullable Integer level, @Nullable String levelTemplate) {
        this.level = level;
        this.levelTemplate = levelTemplate;
    }

    @Nullable
    public static LevelComponent from(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            return NBT.getComponents(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(COMPONENT_KEY + "." + MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".level");
                if (data == null) return null;
                return new LevelComponent(data.getInteger("lvl"), data.getString("template"));
            });
        } else {
            return NBT.get(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(TAG_KEY + "." + TERRA_COMPONENT_KEY + ".level");
                if (data == null) return null;
                return new LevelComponent(data.getInteger("lvl"), data.getString("template"));
            });
        }
    }

    @Override
    public void apply(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                ReadWriteNBT data = nbt.resolveOrCreateCompound(COMPONENT_KEY + "." + MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".level");
                if (level != null) data.setInteger("lvl", level);
                if (levelTemplate != null) data.setString("template", levelTemplate);
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt -> {
                ReadWriteNBT data = nbt.resolveOrCreateCompound(TAG_KEY + "." + TERRA_COMPONENT_KEY + ".level");
                if (level != null) data.setInteger("lvl", level);
                if (levelTemplate != null) data.setString("template", levelTemplate);
            });
        }
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
}
