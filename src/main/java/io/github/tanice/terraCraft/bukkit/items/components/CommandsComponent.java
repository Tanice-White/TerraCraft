package io.github.tanice.terraCraft.bukkit.items.components;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.ComponentState;
import io.github.tanice.terraCraft.api.items.components.TerraCommandsComponent;
import io.github.tanice.terraCraft.api.items.components.AbstractItemComponent;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * 消耗物品的额外拓展
 */
public class CommandsComponent extends AbstractItemComponent implements TerraCommandsComponent {
    @Nullable
    private List<String> commands;

    public CommandsComponent(@Nullable List<String> commands, boolean updatable) {
        super(updatable);
        this.commands = commands;
    }

    public CommandsComponent(@Nullable List<String> commands, ComponentState state) {
        super(state);
        this.commands = commands;
    }

    public CommandsComponent(ConfigurationSection cfg) {
        super(cfg.getBoolean("updatable", true));
        this.commands = cfg.getStringList("commands");
    }

    @Nullable
    public static CommandsComponent from(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)){
            return NBT.getComponents(item, nbt ->{
                ReadableNBT data = nbt.resolveCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".commands");
                if (data == null) return null;
                return new CommandsComponent(data.getStringList("content").toListCopy(), new ComponentState(data.getByte("state")));
            });
        } else {
            return NBT.modify(item, nbt -> {
                ReadWriteNBT data = nbt.resolveOrCreateCompound(TERRA_COMPONENT_KEY + ".commands");
                if (data == null) return null;
                return new CommandsComponent(data.getStringList("content").toListCopy(), new ComponentState(data.getByte("state")));
            });
        }
    }

    @Override
    public void doApply(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)){
            NBT.modifyComponents(item, nbt ->{
                ReadWriteNBT component = nbt.resolveOrCreateCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".commands");
                component.setByte("state", state.toNbtByte());
                component.getStringList("content").addAll(commands);
            });
        } else {
            NBT.modify(item, nbt -> {
                ReadWriteNBT component = nbt.resolveOrCreateCompound(TERRA_COMPONENT_KEY + ".commands");
                component.setByte("state", state.toNbtByte());
                component.getStringList("content").addAll(commands);
            });
        }
    }

    @Override
    public void callEvent() {

    }

    @Override
    public void updateLore() {

    }

    public static void clear(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)){
            NBT.modifyComponents(item.getBukkitItem(), nbt ->{
                nbt.resolveOrCreateCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY).removeKey("commands");
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt -> {
                nbt.resolveOrCreateCompound(TERRA_COMPONENT_KEY).removeKey("commands");
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
