package io.github.tanice.terraCraft.bukkit.utils.attributes;

import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static io.github.tanice.terraCraft.bukkit.utils.attributes.AttributeKeys.*;

/**
 * TODO AttributeAPI 对接游戏原版属性  需要兼容
 * 有关更改物品原版属性的一切都需要通过此类实现
 */
public final class AttributeAPI {
    private static final Map<String, Attribute> ATTRIBUTE_MAP = new HashMap<>();

    /* 被主动设置的原版属性的Namespace的key */
    private static final String SET_NAMESPACE_SUFFIX = "-set";
    private static final String ADD_NAMESPACE_SUFFIX = "-add";

    /** 移除所有原版属性 */
    @Deprecated
    public static void removeAllAttr(ItemMeta meta) {
        meta.setAttributeModifiers(null);
    }

    /**
     * 设置物品的某一个原版属性
     * 根据预设设置对应的 ATTRIBUTE 移除所有 ATTRIBUTE 下的所有 modifier ，然后直接创建一个新的 modifier
     * 一般在读取配置文件时使用
     *
     * @param attrKey 配置文件中的ATTR类关键词
     * @param meta    物品的meta值
     * @param v       对应属性需要修改成的值
     * @param slot    生效的位置
     */
    public static void setAttr(String from, ItemMeta meta, String attrKey, String op, double v, EquipmentSlotGroup slot) {
        Attribute a = ATTRIBUTE_MAP.get(attrKey);
        if (a == null) return;
        meta.removeAttributeModifier(a);
        if (op.equalsIgnoreCase("+")) {
            meta.addAttributeModifier(a, new AttributeModifier(
                    new NamespacedKey(from + SET_NAMESPACE_SUFFIX, attrKey),
                    v,
                    AttributeModifier.Operation.ADD_NUMBER,
                    slot
            ));
        } else {
            meta.addAttributeModifier(a, new AttributeModifier(
                    new NamespacedKey(from + SET_NAMESPACE_SUFFIX, attrKey),
                    v,
                    AttributeModifier.Operation.ADD_SCALAR,
                    slot
            ));
        }
    }

    /**
     * 获取一个属性下的所有 modifiers
     *
     * @param meta   物品
     * @param attrKey Attribute的TwItems的内部关键词
     * @return List<AttributeModifier>
     */
    public static Collection<AttributeModifier> getAttrModifiers(ItemMeta meta, String attrKey) {
        Attribute a = ATTRIBUTE_MAP.get(attrKey);
        if (a == null) return null;
        return meta.getAttributeModifiers(a);
    }

    /**
     * 获取某个被本插件set过的 AttributeModifier(最多只有一个)
     *
     * @param meta   物品
     * @param attrKey Attribute的TwItems的内部关键词
     * @return 数值和生效槽位的Tuple
     */
    public static AttributeModifier getCustomSetAttr(ItemMeta meta, String namespaceKey, String attrKey) {
        Attribute a = ATTRIBUTE_MAP.get(attrKey);
        if (a == null) return null;
        Collection<AttributeModifier> attributeModifiers = meta.getAttributeModifiers(a);
        if (attributeModifiers == null || attributeModifiers.isEmpty()) return null;
        for (AttributeModifier am : attributeModifiers) {
            if (am.getKey().getNamespace().equals(namespaceKey + SET_NAMESPACE_SUFFIX)) return am;
        }
        return null;
    }

    /**
     * 给物品增加 add属性
     * 物品属性给物品本身的增值
     *
     * @param from   属性来源作为NamespaceKey
     * @param meta   手持物 meta 信息
     * @param attrKey 需要增加的Attribute的TwItems的内部关键词
     * @param v      需要增加的属性值
     * @param slot   生效槽位, 默认为主手
     */
    @Deprecated
    public static void addAttr(String from, ItemMeta meta, String attrKey, String act, double v, EquipmentSlotGroup slot) {
        Attribute a = ATTRIBUTE_MAP.get(attrKey);
        if (a == null) return;
        if (act.equals("+")) {
            meta.addAttributeModifier(a, new AttributeModifier(
                    new NamespacedKey(from + ADD_NAMESPACE_SUFFIX, attrKey),
                    v,
                    AttributeModifier.Operation.ADD_NUMBER,
                    slot
            ));
        } else {
            meta.addAttributeModifier(a, new AttributeModifier(
                    new NamespacedKey(from + ADD_NAMESPACE_SUFFIX, attrKey),
                    v,
                    AttributeModifier.Operation.ADD_SCALAR,
                    slot
            ));
        }
    }

    /**
     * 获取增加的属性
     * @param key 属性的NamespaceKey
     * @param meta 物品 Meta 数据
     * @param attrKey Attribute的TwItems的内部关键词
     * @return AttributeModifier
     */
    public static AttributeModifier getCustomAddAttrByKey(String key, ItemMeta meta, String attrKey) {
        Attribute a = ATTRIBUTE_MAP.get(attrKey);
        if (a == null) return null;
        Collection<AttributeModifier> attributeModifiers = meta.getAttributeModifiers(a);
        if (attributeModifiers == null || attributeModifiers.isEmpty()) return null;
        for (AttributeModifier am : attributeModifiers) {
            if (am.getKey().getNamespace().equals(key + ADD_NAMESPACE_SUFFIX)) return am;
        }
        return null;
    }

    /**
     * 删除已存在的 add 属性
     *
     * @param meta 物品 Meta 数据
     * @param am   已存在的 add属性
     */
    public static void removeAttr(ItemMeta meta, String attrKey, AttributeModifier am) {
        Attribute a = ATTRIBUTE_MAP.get(attrKey);
        if (a == null) return;
        boolean ok = meta.removeAttributeModifier(a, am);
        if (!ok) {
            TerraCraftLogger.error("Failed to remove attribute: " + attrKey + " from " + meta.getDisplayName());
        }
    }

    /**
     * 根据 NamespaceKey 删除已存在的 add 属性
     *
     * @param key     NamespaceKey
     * @param meta    物品 Meta 数据
     * @param attrKey 属性名称
     */
    public static void removeAddAttrByKey(String key, ItemMeta meta, String attrKey) {
        AttributeModifier amd = getCustomAddAttrByKey(key, meta, attrKey);
        if (amd == null) return;
        removeAttr(meta, attrKey, amd);
    }

    /**
     * 获取实体的原版属性
     */
    public static double getOriBaseAttr(LivingEntity entity, Attribute attribute) {
        AttributeInstance attrInstance = entity.getAttribute(attribute);
        return attrInstance != null ? attrInstance.getBaseValue() : 0.0;
    }

    /**
     * 设置实体的原版属性
     */
    public static void setOriBaseAttr(LivingEntity entity, Attribute attribute, double baseValue) {
        AttributeInstance attrInstance = entity.getAttribute(attribute);
        if (attrInstance != null) attrInstance.setBaseValue(baseValue);
    }

    static {
        //ATTRIBUTE_MAP.put(BASE_DAMAGE, Attribute.ATTACK_DAMAGE);
        ATTRIBUTE_MAP.put(ATTACK_SPEED, Attribute.ATTACK_SPEED);
        ATTRIBUTE_MAP.put(ATTACK_KNOCKBACK, Attribute.ATTACK_KNOCKBACK);
        ATTRIBUTE_MAP.put(KNOCKBACK_RESISTANCE, Attribute.KNOCKBACK_RESISTANCE);
        //ATTRIBUTE_MAP.put(ARMOR, Attribute.ARMOR);
        //ATTRIBUTE_MAP.put(ARMOR_TOUGHNESS, Attribute.ARMOR_TOUGHNESS);
        ATTRIBUTE_MAP.put(MOVEMENT_SPEED, Attribute.MOVEMENT_SPEED);
        ATTRIBUTE_MAP.put(LUCK, Attribute.LUCK);
        ATTRIBUTE_MAP.put(MAX_HEALTH, Attribute.MAX_HEALTH);
        ATTRIBUTE_MAP.put(BLOCK_BREAK_SPEED, Attribute.BLOCK_BREAK_SPEED);
        ATTRIBUTE_MAP.put(BLOCK_INTERACTION_RANGE, Attribute.BLOCK_INTERACTION_RANGE);
        ATTRIBUTE_MAP.put(ENTITY_INTERACTION_RANGE, Attribute.ENTITY_INTERACTION_RANGE);
        ATTRIBUTE_MAP.put(MINING_EFFICIENCY, Attribute.MINING_EFFICIENCY);
        /* 没啥用的部分 */
        ATTRIBUTE_MAP.put(BURNING_TIME, Attribute.BURNING_TIME);
        ATTRIBUTE_MAP.put(EXPLOSION_KNOCKBACK_RESISTANCE, Attribute.EXPLOSION_KNOCKBACK_RESISTANCE);
        ATTRIBUTE_MAP.put(FALL_DAMAGE_MULTIPLIER, Attribute.FALL_DAMAGE_MULTIPLIER);
        ATTRIBUTE_MAP.put(FLYING_SPEED, Attribute.FLYING_SPEED);
        ATTRIBUTE_MAP.put(GRAVITY, Attribute.GRAVITY);
        ATTRIBUTE_MAP.put(JUMP_STRENGTH, Attribute.JUMP_STRENGTH);
        ATTRIBUTE_MAP.put(MAX_ABSORPTION, Attribute.MAX_ABSORPTION);
        ATTRIBUTE_MAP.put(MOVEMENT_EFFICIENCY, Attribute.MOVEMENT_EFFICIENCY);
        ATTRIBUTE_MAP.put(OXYGEN_BONUS, Attribute.OXYGEN_BONUS);
        ATTRIBUTE_MAP.put(SAFE_FALL_DISTANCE, Attribute.SAFE_FALL_DISTANCE);
        ATTRIBUTE_MAP.put(SCALE, Attribute.SCALE);
        ATTRIBUTE_MAP.put(STEP_HEIGHT, Attribute.STEP_HEIGHT);
        ATTRIBUTE_MAP.put(WATER_MOVEMENT_EFFICIENCY, Attribute.WATER_MOVEMENT_EFFICIENCY);
        ATTRIBUTE_MAP.put(SNEAKING_SPEED, Attribute.SNEAKING_SPEED);
        ATTRIBUTE_MAP.put(SUBMERGED_MINING_SPEED, Attribute.SUBMERGED_MINING_SPEED);
        ATTRIBUTE_MAP.put(SWEEPING_DAMAGE_RATIO, Attribute.SWEEPING_DAMAGE_RATIO);
    }
}
