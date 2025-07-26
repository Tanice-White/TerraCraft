package io.github.tanice.terraCraft.api.attribute;

public interface TerraCalculableMeta {
    /**
     * 按照k倍合并目标同类属性值和同类伤害类型值到自身
     * @param meta 目标属性
     * @param k 倍率
     */
    void merge(TerraCalculableMeta meta, int k);

    /**
     * 获取生效的计算区
     * @return 计算区枚举
     */
    AttributeActiveSection getActiveSection();

    /**
     * 获取目标属性的计算值
     * @param type 目标属性
     * @return 属性数值
     */
    double get(AttributeType type);

    /**
     * 获取目标伤害类型计算值
     * @param type 目标伤害类型
     * @return 伤害类型数值
     */
    double get(DamageFromType type);

    /**
     * 获取属性数组的引用
     * @return 属性数组
     */
    double[] getAttributeModifierArray();

    /**
     * 获取伤害类型数组的引用
     * @return 伤害类型数组
     */
    double[] getDamageTypeModifierArray();

    /**
     * 克隆自身
     * @return 自身的可变副本
     */
    TerraCalculableMeta clone();
}
