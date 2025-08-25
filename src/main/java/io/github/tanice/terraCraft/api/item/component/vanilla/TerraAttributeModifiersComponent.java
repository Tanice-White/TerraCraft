package io.github.tanice.terraCraft.api.item.component.vanilla;

import io.github.tanice.terraCraft.api.item.component.TerraBaseComponent;

import javax.annotation.Nullable;

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
