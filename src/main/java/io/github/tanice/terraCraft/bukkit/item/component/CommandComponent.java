package io.github.tanice.terraCraft.bukkit.item.component;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import io.github.tanice.terraCraft.api.item.TerraBaseItem;
import io.github.tanice.terraCraft.api.item.component.ComponentState;
import io.github.tanice.terraCraft.api.item.component.TerraCommandsComponent;
import io.github.tanice.terraCraft.api.item.component.AbstractItemComponent;
import io.github.tanice.terraCraft.bukkit.util.version.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.util.version.ServerVersion;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * 消耗物品的额外拓展
 */
public class CommandComponent extends AbstractItemComponent implements TerraCommandsComponent {
    @Nullable
    private List<String> commands;

    public CommandComponent(@Nullable List<String> commands, boolean updatable) {
        super(updatable);
        this.commands = commands;
    }

    public CommandComponent(@Nullable List<String> commands, ComponentState state) {
        super(state);
        this.commands = commands;
    }

    public CommandComponent(ConfigurationSection cfg) {
        super(cfg.getBoolean("updatable", true));
        this.commands = cfg.getStringList("content");
    }

    @Nullable
    public static CommandComponent from(ItemStack item) {
        if (item == null || item.isEmpty()) return null;
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)){
            return NBT.getComponents(item, nbt ->{
                ReadableNBT data = nbt.resolveCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".command");
                if (data == null) return null;
                return new CommandComponent(data.getStringList("content").toListCopy(), new ComponentState(data.getByte("state")));
            });
        } else {
            return NBT.modify(item, nbt -> {
                ReadWriteNBT data = nbt.resolveOrCreateCompound(TERRA_COMPONENT_KEY + ".command");
                if (data == null) return null;
                return new CommandComponent(data.getStringList("content").toListCopy(), new ComponentState(data.getByte("state")));
            });
        }
    }

    @Override
    public void doApply(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)){
            NBT.modifyComponents(item, nbt ->{
                ReadWriteNBT component = nbt.resolveOrCreateCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".command");
                component.setByte("state", state.toNbtByte());
                component.getStringList("content").addAll(commands);
            });
        } else {
            NBT.modify(item, nbt -> {
                ReadWriteNBT component = nbt.resolveOrCreateCompound(TERRA_COMPONENT_KEY + ".command");
                component.setByte("state", state.toNbtByte());
                component.getStringList("content").addAll(commands);
            });
        }
    }

    @Override
    public void updateLore() {

    }

    public static void clear(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)){
            NBT.modifyComponents(item.getBukkitItem(), nbt ->{
                nbt.resolveOrCreateCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY).removeKey("command");
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt -> {
                nbt.resolveOrCreateCompound(TERRA_COMPONENT_KEY).removeKey("command");
            });
        }
    }

    public static void remove(TerraBaseItem item) {
        clear(item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commands);
    }

    @Override
    public List<String> getCommands() {
        return this.commands == null ? List.of() : this.commands;
    }

    @Override
    public void setCommands(@Nullable List<String> commands) {
        this.commands = commands;
    }

    @Override
    public String getComponentName() {
        return "command";
    }
}
