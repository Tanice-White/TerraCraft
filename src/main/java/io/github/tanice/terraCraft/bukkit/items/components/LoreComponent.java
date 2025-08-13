package io.github.tanice.terraCraft.bukkit.items.components;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBTCompoundList;
import de.tr7zw.nbtapi.iface.ReadWriteNBTList;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.vanilla.TerraLoreComponent;
import io.github.tanice.terraCraft.bukkit.utils.MiniMessageUtil;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import net.kyori.adventure.text.Component;

import java.util.List;

public class LoreComponent implements TerraLoreComponent {

    List<Component> lore;

    @Override
    public void apply(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt ->{
                ReadWriteNBTCompoundList compoundList = nbt.getOrCreateCompound(COMPONENT_KEY).getCompoundList(MINECRAFT_PREFIX + "lore");
                for (Component c : lore) {
                    compoundList.addCompound().mergeCompound(NBT.parseNBT(MiniMessageUtil.toNBTJson(c)));
                }
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt ->{
                ReadWriteNBTList<String> loreList = nbt.getOrCreateCompound(TAG_KEY).getOrCreateCompound("display").getStringList("lore");
                for (Component c : lore) loreList.add(MiniMessageUtil.toNBTJson(c));
            });
        }
    }
}
