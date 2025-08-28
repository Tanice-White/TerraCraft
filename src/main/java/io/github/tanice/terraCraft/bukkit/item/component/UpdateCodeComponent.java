package io.github.tanice.terraCraft.bukkit.item.component;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import io.github.tanice.terraCraft.api.item.component.TerraUpdateCodeComponent;
import io.github.tanice.terraCraft.bukkit.util.version.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.util.version.ServerVersion;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

import static io.github.tanice.terraCraft.api.command.TerraCommand.*;

public class UpdateCodeComponent implements TerraUpdateCodeComponent {

    private int code;

    public UpdateCodeComponent(int code) {
        this.code = code;
    }

    public static UpdateCodeComponent from(ItemStack item) {
        if (item == null || item.isEmpty()) return null;
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            return NBT.getComponents(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY);
                if (data == null) return null;
                return new UpdateCodeComponent(data.getInteger("code"));
            });
        } else {
            return NBT.get(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(TERRA_COMPONENT_KEY);
                if (data == null) return null;
                return new UpdateCodeComponent(data.getInteger("code"));
            });
        }
    }

    @Override
    public void cover(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt -> {
                nbt.resolveOrCreateCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY).setInteger("code", code);
            });
        } else {
            NBT.modify(item, nbt -> {
                nbt.resolveOrCreateCompound(TERRA_COMPONENT_KEY).setInteger("code", code);
            });
        }
    }

    @Override
    public String getComponentName() {
        return "code";
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return BOLD + YELLOW + "update_code:" + code + RESET;
    }
}
