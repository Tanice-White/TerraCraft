package io.github.tanice.terraCraft.bukkit.item.component;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import io.github.tanice.terraCraft.api.item.component.ComponentState;
import io.github.tanice.terraCraft.api.item.component.TerraSkillComponent;
import io.github.tanice.terraCraft.api.item.component.AbstractItemComponent;
import io.github.tanice.terraCraft.bukkit.util.version.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.util.version.ServerVersion;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

import static io.github.tanice.terraCraft.api.command.TerraCommand.*;

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
        this.skills = cfg.getStringList("content");
    }

    public static SkillComponent from(ItemStack item) {
        if (item == null || item.isEmpty()) return null;
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            return NBT.getComponents(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".skill");
                if (data == null) return null;
                return fromNBT(data);
            });
        } else {
            return NBT.get(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(TERRA_COMPONENT_KEY + ".skill");
                if (data == null) return null;
                return fromNBT(data);
            });
        }
    }

    @Override
    protected void doCover(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt -> {
                ReadWriteNBT data = nbt.resolveOrCreateCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".skill");
                addToCompound(data);
            });
        } else {
            NBT.modify(item, nbt -> {
                ReadWriteNBT data = nbt.resolveOrCreateCompound(TERRA_COMPONENT_KEY + ".skill");
                addToCompound(data);
            });
        }
    }

    @Override
    protected void updateLore() {

    }

    public static void clear(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt -> {
                nbt.resolveOrCreateCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY).removeKey("skill");
            });
        } else {
            NBT.modify(item, nbt -> {
                nbt.resolveOrCreateCompound(TERRA_COMPONENT_KEY).removeKey("skill");
            });
        }
    }

    public static void remove(ItemStack item) {
        clear(item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(skills);
    }

    @Override
    public List<String> getSkills() {
        return this.skills == null ? List.of() : this.skills;
    }

    @Override
    public void setSkills(@Nullable List<String> skills) {
        this.skills = skills;
    }


    @Override
    public String getComponentName() {
        return "skill";
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(BOLD).append(YELLOW).append("skill:").append("\n");
        sb.append("    ").append(AQUA).append("content:");
        List<String> skillList = getSkills();
        if (skillList.isEmpty()) sb.append(GRAY).append("null").append("\n");
        else {
            sb.append(WHITE);
            for (int i = 0; i < skillList.size(); i++) {
                sb.append(skillList.get(i));
                if (i != skillList.size() - 1) sb.append(", ");
            }
        }
        sb.append("    ").append(AQUA).append("state:").append(WHITE).append(state).append(RESET);
        return sb.toString();
    }

    private void addToCompound(ReadWriteNBT compound) {
        if (skills != null && !skills.isEmpty()) {
            compound.getStringList("content").clear();
            compound.getStringList("content").addAll(skills);
        }
        compound.setByte("state", state.toNbtByte());
    }

    private static SkillComponent fromNBT(ReadableNBT nbt) {
        return new SkillComponent(
                nbt.getStringList("content").toListCopy(),
                new ComponentState(nbt.getByte("state"))
        );
    }
}
