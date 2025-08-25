package io.github.tanice.terraCraft.core.item;

import io.github.tanice.terraCraft.api.item.TerraBaseItem;
import io.github.tanice.terraCraft.api.item.component.TerraBaseComponent;
import io.github.tanice.terraCraft.bukkit.item.component.vanilla.*;
import io.github.tanice.terraCraft.bukkit.util.MiniMessageUtil;
import io.github.tanice.terraCraft.core.util.namespace.TerraNamespaceKey;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;


import java.util.*;

import static io.github.tanice.terraCraft.core.util.EnumUtil.safeValueOf;

public abstract class AbstractItem implements TerraBaseItem {
    protected final Material material;
    protected final int amount;
    protected final List<TerraBaseComponent> vanillaComponents;
    protected final ItemStack bukkitItem;

    public AbstractItem(ConfigurationSection cfg) {
        Objects.requireNonNull(cfg, "Item configurationSection cannot be null");
        this.material = safeValueOf(Material.class, cfg.getString("id"), Material.STONE);
        this.amount = Math.max(cfg.getInt("amount"), 1);
        this.vanillaComponents = new ArrayList<>();
        this.bukkitItem = new ItemStack(material, amount);
        this.processComponents(cfg);
        for (TerraBaseComponent component : vanillaComponents) component.apply(bukkitItem);
    }

    public Set<TerraBaseComponent> getVanillaComponents() {
        return Set.copyOf(vanillaComponents);
    }

    @Override
    public ItemStack getBukkitItem() {
        return this.bukkitItem.clone();
    }

    @Override
    public int getHashCode() {
        return Objects.hash(material, amount, vanillaComponents);
    }

    @Override
    public boolean updateOld(ItemStack old) {
        for (TerraBaseComponent component : vanillaComponents) {
            if (!component.canUpdate()) continue;
            component.updatePartial().apply(old);
        }
        return true;
    }

    private void processComponents(ConfigurationSection cfg) {
        ConfigurationSection sub;
        if (cfg.isSet("attributes")) {
            sub = cfg.getConfigurationSection("attributes");
            if (sub != null) vanillaComponents.add(new AttributeModifiersComponent(sub));
        }
        if (cfg.isSet("!attributes")) AttributeModifiersComponent.remove(this);
        if (cfg.isSet("shield")) {
            sub = cfg.getConfigurationSection("shield");
            if (sub != null) vanillaComponents.add(new BlocksAttacksComponent(sub));
        }
        if (cfg.isSet("!shield")) BlocksAttacksComponent.remove(this);
        if (cfg.isSet("break_sound")) {
            sub = cfg.getConfigurationSection("break_sound");
            if (sub != null) vanillaComponents.add(new BreakSoundComponent(sub));
        }
        if (cfg.isSet("!break_sound")) BreakSoundComponent.remove(this);
        if (cfg.isSet("consumable")) {
            sub = cfg.getConfigurationSection("consumable");
            if (sub != null) vanillaComponents.add(new ConsumableComponent(sub));
        }
        if (cfg.isSet("!consumable")) ConsumableComponent.remove(this);
        if (cfg.isSet("custom_data")) {
            sub = cfg.getConfigurationSection("custom_data");
            if (sub != null) vanillaComponents.add(new CustomDataComponent(sub));
        }
        if (cfg.isSet("!custom_data")) CustomDataComponent.remove(this);
        if (cfg.isSet("custom_model_data")) {
            vanillaComponents.add(new CustomModelDataComponent(cfg.getInt("custom_model_data")));
        }
        if (cfg.isSet("!custom_model_data")) CustomModelDataComponent.remove(this);
        if (cfg.isSet("display_name")) {
            vanillaComponents.add(new CustomNameComponent(cfg.getString("display_name")));
        }
        if (cfg.isSet("!display_name")) CustomNameComponent.remove(this);
        if (cfg.isSet("ori_durability")) {
            sub = cfg.getConfigurationSection("ori_durability");
            if (sub != null) vanillaComponents.add(new DamageComponent(sub));
        }
        if (cfg.isSet("!ori_durability")) DamageComponent.remove(this);
        if (cfg.isSet("resistant")) {
            vanillaComponents.add(new DamageResistantComponent(TerraNamespaceKey.from(cfg.getString("resistant"))));
        }
        if (cfg.isSet("!resistant")) DamageResistantComponent.remove(this);
        if (cfg.isSet("death_protection")) {
            sub = cfg.getConfigurationSection("death_protection");
            if (sub != null) vanillaComponents.add(new DeathProtectionComponent(sub));
        }
        if (cfg.isSet("!death_protection")) DeathProtectionComponent.remove(this);
        if (cfg.isSet("color")) {
            vanillaComponents.add(new DyedColorComponent(cfg.getString("color")));
        }
        if (cfg.isSet("!color")) DyedColorComponent.remove(this);

        if (cfg.isSet("enchant")) {
            sub = cfg.getConfigurationSection("enchant");
            if (sub != null) vanillaComponents.add(new EnchantComponent(sub));
        }
        if (cfg.isSet("!enchant")) EnchantComponent.remove(this);
        if (cfg.isSet("glint")) {
            vanillaComponents.add(new EnchantmentGlintOverrideComponent(cfg.getBoolean("glint")));
        }
        if (cfg.isSet("!glint")) EnchantmentGlintOverrideComponent.remove(this);
        if (cfg.isSet("equippable")) {
            sub = cfg.getConfigurationSection("equippable");
            if (sub != null) vanillaComponents.add(new EquippableComponent(sub));
        }
        if (cfg.isSet("!equippable")) EquippableComponent.remove(this);
        if (cfg.isSet("food")) {
            sub = cfg.getConfigurationSection("food");
            if (sub != null) vanillaComponents.add(new FoodComponent(sub));
        }
        if (cfg.isSet("!food")) FoodComponent.remove(this);
        if (cfg.isSet("glider")) vanillaComponents.add(new GliderComponent());
        if (cfg.isSet("!glider")) GliderComponent.remove(this);
        if (cfg.isSet("music_disc")) {
            vanillaComponents.add(new JukeboxPlayable(cfg.getString("music_disc")));
        }
        if (cfg.isSet("!music_disc")) JukeboxPlayable.remove(this);
        if (cfg.isSet("lore")) {
            vanillaComponents.add(new LoreComponent(cfg.getStringList("lore").stream().map(MiniMessageUtil::serialize).toList()));
        }
        if (cfg.isSet("!lore")) LoreComponent.remove(this);
        if (cfg.isSet("max_stack_size")) {
            vanillaComponents.add(new MaxStackSizeComponent(cfg.getInt("max_stack_size")));
        }
        if (cfg.isSet("!max_stack_size")) MaxStackSizeComponent.remove(this);
        if (cfg.isSet("potion")) {
            sub = cfg.getConfigurationSection("potion");
            if (sub != null) vanillaComponents.add(new PotionComponent(sub));
        }
        if (cfg.isSet("!potion")) PotionComponent.remove(this);
        if (cfg.isSet("rarity")) {
            vanillaComponents.add(new RarityComponent(cfg.getString("rarity")));
        }
        if (cfg.isSet("!rarity")) RarityComponent.remove(this);
        if (cfg.isSet("repair")) {
            sub = cfg.getConfigurationSection("repair");
            if (sub != null) vanillaComponents.add(new RepairComponent(sub));
        }
        if (cfg.isSet("!repair")) RepairComponent.remove(this);
        if (cfg.isSet("tool")) {
            sub = cfg.getConfigurationSection("tool");
            if (sub != null) vanillaComponents.add(new ToolComponent(sub));
        }
        if (cfg.isSet("!tool")) ToolComponent.remove(this);
        if (cfg.isSet("tooltip")) {
            sub = cfg.getConfigurationSection("tooltip");
            if (sub != null) vanillaComponents.add(new TooltipComponent(sub));
        }
        if (cfg.isSet("!tooltip")) TooltipComponent.remove(this);
        if (cfg.isSet("use_cooldown")) {
            sub = cfg.getConfigurationSection("use_cooldown");
            if (sub != null) vanillaComponents.add(new UseCooldownComponent(sub));
        }
        if (cfg.isSet("!use_cooldown")) UseCooldownComponent.remove(this);
        if (cfg.isSet("use_remainder")) {
            sub = cfg.getConfigurationSection("use_remainder");
            if (sub != null) vanillaComponents.add(new UseRemainderComponent(sub));
        }
        if (cfg.isSet("!use_remainder")) UseRemainderComponent.remove(this);
        if (cfg.isSet("weapon")) {
            sub = cfg.getConfigurationSection("weapon");
            if (sub != null) vanillaComponents.add(new WeaponComponent(sub));
        }
        if (cfg.isSet("!weapon")) WeaponComponent.remove(this);
    }
}
