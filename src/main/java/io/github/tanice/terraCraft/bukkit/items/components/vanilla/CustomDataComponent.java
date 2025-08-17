package io.github.tanice.terraCraft.bukkit.items.components.vanilla;

import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.vanilla.TerraCustomDataComponent;
import io.github.tanice.terraCraft.core.utils.namespace.TerraNamespaceKey;
import org.bukkit.configuration.ConfigurationSection;

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
    public void apply(TerraBaseItem item) {

    }

    public static void clear(TerraBaseItem item) {

    }

    public static void remove(TerraBaseItem item) {

    }

    @Override
    public int hashCode() {
        return 0;
    }
}
