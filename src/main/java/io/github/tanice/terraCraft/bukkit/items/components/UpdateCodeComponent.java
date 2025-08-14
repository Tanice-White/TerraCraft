package io.github.tanice.terraCraft.bukkit.items.components;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.TerraUpdateCodeComponent;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import org.bukkit.inventory.ItemStack;

public class UpdateCodeComponent implements TerraUpdateCodeComponent {

    private String code;

    public UpdateCodeComponent(String code) {
        this.code = code;
    }

    public static UpdateCodeComponent from(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            return NBT.getComponents(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(COMPONENT_KEY + "." + MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY);
                if (data == null) return null;
                return new UpdateCodeComponent(data.getString("code"));
            });
        } else {
            return NBT.get(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(TAG_KEY + "." + TERRA_COMPONENT_KEY);
                if (data == null) return null;
                return new UpdateCodeComponent(data.getString("code"));
            });
        }
    }

    @Override
    public void apply(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.resolveOrCreateCompound(COMPONENT_KEY + "." + MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY).setString("code", code);
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt -> {
                nbt.resolveOrCreateCompound(TAG_KEY + "." + TERRA_COMPONENT_KEY).setString("code", code);
            });
        }
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }
}
