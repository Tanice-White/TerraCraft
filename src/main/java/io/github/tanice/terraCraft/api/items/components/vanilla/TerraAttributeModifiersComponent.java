package io.github.tanice.terraCraft.api.items.components.vanilla;

import io.github.tanice.terraCraft.api.items.components.TerraBaseComponent;
import io.github.tanice.terraCraft.bukkit.utils.adapter.BukkitAttribute;
import net.kyori.adventure.text.Component;

import javax.annotation.Nullable;

import java.util.HashMap;

public interface TerraAttributeModifiersComponent extends TerraBaseComponent {

    void addAttributeModifier(String id, BukkitAttribute attribute, double amount, String op, @Nullable String slot, @Nullable DisplayType displayType, @Nullable Component extraValue);

    enum Operation {
        ADD(0),
        ADD_NUMBER(0),
        MULTIPLY_BASE(1),
        ADD_SCALAR(1),
        MULTIPLY(2),
        MULTIPLY_SCALAR(2);

        private final int operation;
        private static final HashMap<String, Operation> BY_ALIAS;

        Operation(int operation) {
            this.operation = operation;
        }

        @Nullable
        public static Operation parse(String value) {
            return BY_ALIAS.get(value);
        }

        public int getOperation() {
            return this.operation;
        }

        static {
            BY_ALIAS = new HashMap<>();
            for (Operation value : Operation.values()) {
                BY_ALIAS.put(value.name(), value);
                BY_ALIAS.put(String.valueOf(value.operation), value);
            }
        }
    }

    enum DisplayType {
        DEFAULT,
        HIDDEN,
        OVERRIDE,
    }
}
