package io.github.tanice.terraCraft.bukkit.item.component;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import io.github.tanice.terraCraft.api.item.component.ComponentState;
import io.github.tanice.terraCraft.api.item.component.TerraCommandComponent;
import io.github.tanice.terraCraft.api.item.component.AbstractItemComponent;
import io.github.tanice.terraCraft.bukkit.util.version.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.util.version.ServerVersion;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

import static io.github.tanice.terraCraft.api.command.TerraCommand.*;

/**
 * 消耗物品的额外拓展
 */
public class CommandComponent extends AbstractItemComponent implements TerraCommandComponent {
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
    public void doCover(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)){
            NBT.modifyComponents(item, nbt ->{
                ReadWriteNBT component = nbt.resolveOrCreateCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".command");
                addToCompound(component);
            });
        } else {
            NBT.modify(item, nbt -> {
                ReadWriteNBT component = nbt.resolveOrCreateCompound(TERRA_COMPONENT_KEY + ".command");
                addToCompound(component);
            });
        }
    }

    @Override
    public void updateLore() {

    }

    public static void clear(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)){
            NBT.modifyComponents(item, nbt ->{
                nbt.resolveOrCreateCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY).removeKey("command");
            });
        } else {
            NBT.modify(item, nbt -> {
                nbt.resolveOrCreateCompound(TERRA_COMPONENT_KEY).removeKey("command");
            });
        }
    }

    public static void remove(ItemStack item) {
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(BOLD).append(YELLOW).append("command:").append("\n");
        sb.append(AQUA).append("    ").append("content:").append("\n");
        List<String> commandList = getCommands();
        if (!commandList.isEmpty()) {
            for (int i = 0; i < commandList.size(); i++) {
                sb.append("        ").append(WHITE).append(i + 1).append(" -> ").append(commandList.get(i)).append("\n");
            }
        }
        sb.append(AQUA).append("    ").append("state:").append(WHITE).append(state).append(RESET);
        return sb.toString();
    }

    private void addToCompound(ReadWriteNBT component) {
        component.setByte("state", state.toNbtByte());
        component.getStringList("content").clear();
        component.getStringList("content").addAll(commands);
    }
}
