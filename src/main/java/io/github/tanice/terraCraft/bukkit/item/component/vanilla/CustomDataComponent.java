package io.github.tanice.terraCraft.bukkit.item.component.vanilla;

import io.github.tanice.terraCraft.api.item.component.vanilla.TerraCustomDataComponent;
import io.github.tanice.terraCraft.core.util.namespace.TerraNamespaceKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * TODO 用于拓展额外的 nbt 支持
 */
public class CustomDataComponent implements TerraCustomDataComponent {

    private Map<TerraNamespaceKey, String> customData;

    public CustomDataComponent() {

    }

    public CustomDataComponent(ConfigurationSection cfg) {

    }

    @Override
    public void cover(ItemStack item) {
        clear(item);
    }

    @Override
    public String getComponentName() {
        return "custom_data";
    }

    public static void clear(ItemStack item) {

    }

    public static void remove(ItemStack item) {

    }

    @Override
    public int hashCode() {
        return 0;
    }
}
