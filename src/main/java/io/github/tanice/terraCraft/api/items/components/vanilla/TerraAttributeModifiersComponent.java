package io.github.tanice.terraCraft.api.items.components.vanilla;

import io.github.tanice.terraCraft.api.items.components.TerraBaseComponent;
import io.github.tanice.terraCraft.bukkit.utils.adapter.BukkitAttribute;
import net.kyori.adventure.text.Component;

import javax.annotation.Nullable;

import java.util.HashMap;

public interface TerraAttributeModifiersComponent extends TerraBaseComponent {

    void addAttributeModifier(String id, String attribute, double amount, String op, @Nullable String slot, @Nullable String displayType, @Nullable String extraValue);

    enum Operation {
        ADD_VALUE,
        ADD_MULTIPLY_BASE,
        ADD_MULTIPLY_TOTAL;
    }

    enum DisplayType {
        DEFAULT,
        HIDDEN,
        OVERRIDE,
    }
}
