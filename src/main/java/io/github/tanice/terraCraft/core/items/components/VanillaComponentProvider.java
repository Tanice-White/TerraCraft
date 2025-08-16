package io.github.tanice.terraCraft.core.items.components;

import io.github.tanice.terraCraft.api.items.components.TerraBaseComponent;
import io.github.tanice.terraCraft.bukkit.items.components.vanilla.*;

import java.util.HashMap;
import java.util.Map;

public class VanillaComponentProvider {
    private static final Map<String, Class<? extends TerraBaseComponent>> componentNameMap = new HashMap<>();

    public TerraBaseComponent createComponent(String componentName) {
        Class<? extends TerraBaseComponent> componentClass = componentNameMap.get(componentName);
        componentClass.getConstructor()
    }



    static {
        componentNameMap.put("attributes", AttributeModifiersComponent.class);
        componentNameMap.put("shield", BlocksAttacksComponent.class);
        componentNameMap.put("break_sound", BreakSoundComponent.class);
        componentNameMap.put("consumable", ConsumableComponent.class);
        componentNameMap.put("custom_data", CustomDataComponent.class);
        componentNameMap.put("cmd", CustomModelDataComponent.class);
        componentNameMap.put("display_name", CustomNameComponent.class);
        componentNameMap.put("ori_durability", DamageComponent.class);
        componentNameMap.put("resistant", DamageResistantComponent.class);
        componentNameMap.put("death_protection", DeathProtectionComponent.class);
        componentNameMap.put("color", DyedColorComponent.class);
        componentNameMap.put("enchant", EnchantComponent.class);
        componentNameMap.put("glint", EnchantmentGlintOverrideComponent.class);
        componentNameMap.put("equippable", EquippableComponent.class);
        componentNameMap.put("food", FoodComponent.class);
        componentNameMap.put("glider", GliderComponent.class);
        componentNameMap.put("record", JukeboxPlayable.class);
        componentNameMap.put("lore", LoreComponent.class);
        componentNameMap.put("stack", MaxStackSizeComponent.class);
        componentNameMap.put("potion", PotionComponent.class);
        componentNameMap.put("rarity", RarityComponent.class);
        componentNameMap.put("repair", RepairComponent.class);
        componentNameMap.put("tool", ToolComponent.class);
        componentNameMap.put("tooltip", TooltipComponent.class);
        componentNameMap.put("use_cooldown", UseCooldownComponent.class);
        componentNameMap.put("use_remainder", UseRemainderComponent.class);
        componentNameMap.put("weapon", WeaponComponent.class);
    }
}
