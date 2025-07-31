package io.github.tanice.terraCraft.bukkit.items.components;

import io.github.tanice.terraCraft.api.items.components.TerraCustomModelDataComponent;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 1.21.6 +
 */
public class BukkitCustomModelDataComponent implements TerraCustomModelDataComponent {

    private final List<Integer> cmds;
    private final List<String> strings;
    private final List<Color> colors;

    public BukkitCustomModelDataComponent(@Nullable List<Integer> cmds, @Nullable List<String> strings, @Nullable List<Color> colors) {
        this.cmds = cmds;
        this.strings = strings;
        this.colors = colors;
    }

    @Override
    public void apply(ItemMeta meta) {
        Objects.requireNonNull(meta, "meta should not be null");
        if (cmds == null || cmds.isEmpty()) return;
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_6)) {
            ItemStack item = new ItemStack(Material.AIR);
            item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData()
                    .addFloats(cmds.stream().filter(Objects::nonNull).map(Integer::floatValue).collect(Collectors.toList()))
                    .addStrings(strings).addColors(colors));

        } else meta.setCustomModelData(cmds.getFirst());
    }


    @Override
    public List<Integer> getCustomModelData() {
        if (cmds == null || cmds.isEmpty()) return Collections.emptyList();
        return Collections.unmodifiableList(this.cmds);
    }

    @Override
    public List<String> getStrings() {
        if (strings == null || strings.isEmpty()) return Collections.emptyList();
        return Collections.unmodifiableList(this.strings);
    }

    @Override
    public List<Color> getColors() {
        if (colors == null || colors.isEmpty()) return Collections.emptyList();
        return Collections.unmodifiableList(this.colors);
    }
}
