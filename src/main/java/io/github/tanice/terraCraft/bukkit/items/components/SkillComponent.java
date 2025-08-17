package io.github.tanice.terraCraft.bukkit.items.components;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.ComponentState;
import io.github.tanice.terraCraft.api.items.components.TerraSkillComponent;
import io.github.tanice.terraCraft.api.items.components.AbstractItemComponent;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class SkillComponent extends AbstractItemComponent implements TerraSkillComponent {

    @Nullable
    private List<String> skills;

    public SkillComponent(@Nullable List<String> skills, boolean updatable) {
        super(updatable);
        this.skills = skills;
    }

    public SkillComponent(@Nullable List<String> skills, ComponentState state) {
        super(state);
        this.skills = skills;
    }

    public SkillComponent(ConfigurationSection cfg) {
        super(cfg.getBoolean("updatable", true));
        this.skills = cfg.getStringList("skills");
    }

    public static SkillComponent from(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            return NBT.getComponents(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(COMPONENT_KEY + "." + MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".skills");
                if (data == null) return null;
                return fromNBT(data);
            });
        } else {
            return NBT.get(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(TAG_KEY + "." + TERRA_COMPONENT_KEY + ".skills");
                if (data == null) return null;
                return fromNBT(data);
            });
        }
    }

    @Override
    public void apply(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt -> {
                ReadWriteNBT data = nbt.resolveOrCreateCompound(COMPONENT_KEY + "." + MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".skills");
                addToCompound(data);
            });
        } else {
            NBT.modify(item, nbt -> {
                ReadWriteNBT data = nbt.resolveOrCreateCompound(TAG_KEY + "." + TERRA_COMPONENT_KEY + ".skills");
                addToCompound(data);
            });
        }
    }

    public static void clear(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.resolveOrCreateCompound(COMPONENT_KEY + "." + MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY).removeKey("skills");
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt -> {
                nbt.resolveOrCreateCompound(TAG_KEY + "." + TERRA_COMPONENT_KEY).removeKey("skills");
            });
        }
    }

    public static void remove(TerraBaseItem item) {
        clear(item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(skills);
    }

    @Override
    public @Nullable List<String> getSkills() {
        return this.skills;
    }

    @Override
    public void setSkills(@Nullable List<String> skills) {
        this.skills = skills;
    }

    private void addToCompound(ReadWriteNBT compound) {
        if (skills != null && !skills.isEmpty()) compound.getStringList("value").addAll(skills);
        compound.setByte("state", state.toNbtByte());
    }

    private static SkillComponent fromNBT(ReadableNBT nbt) {
        return new SkillComponent(
                nbt.getStringList("value").toListCopy(),
                new ComponentState(nbt.getByte("state"))
        );
    }
}
