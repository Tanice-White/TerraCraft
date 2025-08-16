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
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

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

    @Nullable
    public static CommandComponent from(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)){
            return NBT.getComponents(item, nbt ->{
                ReadableNBT data = nbt.resolveCompound(COMPONENT_KEY + "." + MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".commands");
                if (data == null) return null;
                return new CommandComponent(data.getStringList("content").toListCopy(), new ComponentState(data.getByte("state")));
            });
        } else {
            return NBT.modify(item, nbt -> {
                ReadWriteNBT data = nbt.resolveOrCreateCompound(TAG_KEY + "." + TERRA_COMPONENT_KEY + ".commands");
                if (data == null) return null;
                return new CommandComponent(data.getStringList("content").toListCopy(), new ComponentState(data.getByte("state")));
            });
        }
    }

    @Override
    public void apply(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)){
            NBT.modifyComponents(item.getBukkitItem(), nbt ->{
                ReadWriteNBT component = nbt.resolveOrCreateCompound(COMPONENT_KEY + "." + MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".commands");
                component.setByte("state", state.toNbtByte());
                component.getStringList("content").addAll(commands);
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt -> {
                ReadWriteNBT component = nbt.resolveOrCreateCompound(TAG_KEY + "." + TERRA_COMPONENT_KEY + ".commands");
                component.setByte("state", state.toNbtByte());
                component.getStringList("content").addAll(commands);
            });
        }
    }

    public static void clear(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)){
            NBT.modifyComponents(item.getBukkitItem(), nbt ->{
                nbt.resolveOrCreateCompound(COMPONENT_KEY + "." + MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY).removeKey("commands");
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt -> {
                nbt.resolveOrCreateCompound(TAG_KEY + "." + TERRA_COMPONENT_KEY).removeKey("commands");
            });
        }
    }

    public static void remove(TerraBaseItem item) {
        clear(item);
    }

    @Override
    public @Nullable List<String> getCommands() {
        return this.commands;
    }

    @Override
    public void setCommands(@Nullable List<String> commands) {
        this.commands = commands;
    }
}
