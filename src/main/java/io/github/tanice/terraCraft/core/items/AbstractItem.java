package io.github.tanice.terraCraft.core.items;

import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.bukkit.events.TerraItemSpawnEvent;
import io.github.tanice.terraCraft.bukkit.utils.MiniMessageUtil;
import io.github.tanice.terraCraft.bukkit.utils.events.TerraEvents;
import io.github.tanice.terraCraft.bukkit.utils.pdc.PDCAPI;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.*;

import static io.github.tanice.terraCraft.core.utils.EnumUtil.safeValueOf;
import static io.github.tanice.terraCraft.core.constants.ConfigKeys.*;

public abstract class AbstractItem implements TerraBaseItem {
    /** 内部名称 */
    protected final String name;
    /** 对应原版材质 */
    protected final Material material;
    /** 原始的展示名称 */
    protected final String displayName;
    /** 描述模板名 */
    protected String loreTemplateName;
    /** 原始描述 */
    protected List<String> lore;  /* 归为 loreTemplate 中的 [Item] 关键词下 */
    protected int amount;
    protected int maxStackSize;
    protected int customModelData;
    protected boolean unbreakable;
    protected int maxDamage;
    protected String color;
    protected List<String> hideFlags;
    protected final Map<String, String> customNBT;

    protected int hashCode;
    protected ItemStack item = new ItemStack(Material.AIR);

    /**
     * 依据内部名称和对应的config文件创建物品
     * @param name 客制化物品内部名称
     * @param cfg 对应的配置文件部分
     */
    public AbstractItem(String name, ConfigurationSection cfg) {
        this.name = name;
        this.displayName = cfg.getString(DISPLAY_NAME, name);
        this.lore = cfg.getStringList(LORE);
        this.loreTemplateName = cfg.getString(LORE_TEMPLATE,"");
        this.customNBT = new HashMap<>();
        this.loadCustomNBT(cfg);

        this.material = safeValueOf(Material.class, cfg.getString(ORI_MATERIAL), Material.STONE);
        this.amount = cfg.getInt(AMOUNT, 1);
        this.maxStackSize = cfg.getInt(MAX_STACK, 1);
        this.unbreakable = cfg.getBoolean(UNBREAKABLE, false);
        this.maxDamage = cfg.getInt(MAX_DURABILITY);
        this.customModelData = cfg.getInt(CUSTOM_MODEL_DATA);
        this.hideFlags = cfg.getStringList(HIDE_FLAGS);
        this.color = cfg.getString(COLOR, "");
        this.hash();
        this.generate();
    }

    public List<String> selfUpdate(ItemStack old) {
        if (old == null) return List.of();
        old.setAmount(amount);
        ItemMeta oldMeta = old.getItemMeta();
        if (oldMeta == null) return List.of();

        oldMeta.setDisplayName(MiniMessageUtil.serialize(displayName).toString());
        oldMeta.setMaxStackSize(maxStackSize);
        oldMeta.setUnbreakable(unbreakable);
        if (customModelData != 0) oldMeta.setCustomModelData(customModelData);
        for (String f : hideFlags) oldMeta.addItemFlags(ItemFlag.valueOf("HIDE_" + f.toUpperCase()));
        if (!color.isEmpty() && oldMeta instanceof LeatherArmorMeta lMeta){
            lMeta.setColor(MiniMessageUtil.gethexColor(color));
        }

        oldMeta.setUnbreakable(unbreakable);
        if (unbreakable) {
            PDCAPI.setMaxDamage(oldMeta, maxDamage);
            PDCAPI.setCurrentDamage(oldMeta, Math.min(PDCAPI.getCurrentDamage(oldMeta), maxDamage));
        }
        removeCustomNBT(oldMeta);
        attachCustomNBT(oldMeta);
        old.setItemMeta(oldMeta);
        return List.of();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public String getLoreTemplateName() {
        return this.loreTemplateName;
    }

    @Override
    public Material type() {
        return this.material;
    }

    @Override
    public int getAmount() {
        return this.amount;
    }

    @Override
    public int getMaxStackSize() {
        return this.maxStackSize;
    }

    @Override
    public int getCustomModelData() {
        return this.customModelData;
    }

    @Override
    public boolean unbreakable() {
        return this.unbreakable;
    }

    @Override
    public int getMaxDamage() {
        return this.maxDamage;
    }

    @Override
    public String getColor() {
        return this.color;
    }

    @Override
    public List<String> getHideFlags() {
        return this.hideFlags;
    }

    @Override
    public Map<String, String> getCustomNBTs() {
        return this.customNBT;
    }

    @Override
    public ItemStack getBukkitItem() {
        ItemStack res = item.clone();
        TerraItemSpawnEvent event = TerraEvents.callAndReturn(new TerraItemSpawnEvent(this, res));
        if (event.isCancelled()) return new ItemStack(Material.AIR);
        return res;
    }

    @Override
    public List<String> getLore() {
        return new ArrayList<>(lore);
    }

    @Override
    public int getHashCode() {
        return this.hashCode;
    }

    private void generate() {
        if (material.isAir()) return;

        item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            TerraCraftLogger.error("Material: " + material + " in Item " + name + " does not have meta data!");
            return;
        }

        meta.setDisplayName(MiniMessageUtil.serialize(displayName).toString());
        meta.setMaxStackSize(maxStackSize);
        meta.setUnbreakable(unbreakable);
        if (customModelData != 0) meta.setCustomModelData(customModelData);
        for (String f : hideFlags) meta.addItemFlags(ItemFlag.valueOf("HIDE_" + f.toUpperCase()));
        if (!color.isEmpty() && meta instanceof LeatherArmorMeta lMeta){
            lMeta.setColor(MiniMessageUtil.gethexColor(color));
        }
        /* 内部名 */
        PDCAPI.setItemName(meta, name);
        /* 原版不可破坏才能使用自定义耐久 */
        if (unbreakable) {
            PDCAPI.setMaxDamage(meta, maxDamage);
            PDCAPI.setCurrentDamage(meta, maxDamage);
        }

        attachCustomNBT(meta);
        PDCAPI.setCode(meta, hashCode);

        item.setItemMeta(meta);
    }

    /**
     * 加载 nbt
     */
    protected void loadCustomNBT(ConfigurationSection cfg) {
        ConfigurationSection sub = cfg.getConfigurationSection(CUSTOM_NBT);
        if (sub == null) return;
        String v;
        for (String key : sub.getKeys(false)) {
            v = sub.getString(key);
            if (v == null) continue;
            customNBT.put(key, v);
        }
    }

    /**
     * 绑定nbt
     */
    protected void attachCustomNBT(ItemMeta meta) {
        for (Map.Entry<String, String> entry : customNBT.entrySet()) {
            if (!PDCAPI.setCustomNBT(meta, entry.getKey(), entry.getValue()))
                TerraCraftLogger.error("Error writing custom nbt [" + entry.getKey() + ":" + entry.getValue() + "] for item: " + this.name);
        }
    }

    /**
     * 清除nbt(即PersistentDataContainer)
     */
    protected void removeCustomNBT(ItemMeta meta) {
        PDCAPI.removeAllCustomNBT(meta);
    }

    protected void hash() {
        this.hashCode = Objects.hash(
                name, material, displayName, loreTemplateName, lore, amount, maxStackSize,
                customModelData, unbreakable, maxDamage, color, hideFlags, customNBT
        );
    }
}
