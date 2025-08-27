package io.github.tanice.terraCraft.bukkit.util;

import io.github.tanice.terraCraft.api.item.component.*;
import io.github.tanice.terraCraft.bukkit.item.component.*;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class TerraComponentUtil {

    public static List<TerraBaseComponent> getTerraComponentFrom(ItemStack item) {
        TerraBaseComponent[] allComponents = {
                TerraNameComponent.from(item),
                UpdateCodeComponent.from(item),
                DamageTypeComponent.from(item),
                SlotComponent.from(item),
                LevelComponent.from(item),
                QualityComponent.from(item),
                DurabilityComponent.from(item),
                GemHolderComponent.from(item),
                GemComponent.from(item),
                BuffComponent.from(item),
                CommandComponent.from(item),
                SkillComponent.from(item),
                MetaComponent.from(item)
        };

        return Arrays.stream(allComponents).filter(Objects::nonNull).toList();
    }
}
