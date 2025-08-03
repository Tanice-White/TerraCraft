package io.github.tanice.terraCraft.bukkit.items.components;

import io.github.tanice.terraCraft.api.items.components.TerraCustomModelDataComponent;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static io.github.tanice.terraCraft.bukkit.utils.StringUtil.splitByComma;
import static io.github.tanice.terraCraft.core.constants.ComponentKeys.*;

/**
 * paper 1.21.6 +
 */
public class BukkitCustomModelDataComponent implements TerraCustomModelDataComponent {

    private List<Integer> cmds;
    private List<String> strings;
    private List<Color> colors;

    public BukkitCustomModelDataComponent(ConfigurationSection cfg) {
        if (cfg == null) return;
        ConfigurationSection section = cfg.getConfigurationSection(CUSTOM_MODEL_DATA_COMPONENT);
        if (section == null) return;
        this.cmds = splitByComma(section.getString(CMDS)).stream().map(Integer::parseInt).toList();
        this.strings = splitByComma(section.getString(STRINGS));
        this.colors = splitByComma(section.getString(COLORS)).stream().map(Integer::parseInt).map(Color::fromRGB).toList();
    }

    @Override
    public void apply(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_6)) {
            CustomModelData.Builder builder = CustomModelData.customModelData();
            if (cmds != null && !cmds.isEmpty()) builder.addFloats(cmds.stream().filter(Objects::nonNull).map(Integer::floatValue).toList());
            if (strings != null && !strings.isEmpty()) builder.addStrings(strings);
            if (colors != null && !colors.isEmpty()) builder.addColors(colors);
            item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, builder);

        } else {
            ItemMeta meta = item.getItemMeta();
            if (meta == null || cmds == null || cmds.isEmpty()) return;
            meta.setCustomModelData(cmds.getFirst());
        }
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
