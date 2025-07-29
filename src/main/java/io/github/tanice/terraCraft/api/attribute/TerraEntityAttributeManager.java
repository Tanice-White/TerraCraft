package io.github.tanice.terraCraft.api.attribute;

import io.github.tanice.terraCraft.api.attribute.calculator.TerraAttributeCalculator;
import org.bukkit.entity.LivingEntity;

public interface TerraEntityAttributeManager {
    /**
     * 获取实体的属性计算结果
     * @param entity 目标实体
     * @return 属性计算器
     */
    TerraAttributeCalculator getAttributeCalculator(LivingEntity entity);

    /**
     * 更新实体属性
     * @param entity 目标实体
     */
    void updateAttribute(LivingEntity entity);

    /**
     * 取消实体属性计算
     *
     * @param entity 目标实体
     */
    void unregister(LivingEntity entity);

    /**
     * 获取服务器中计算过属性的实体数量
     * @return 服务器中计算过属性的实体数量
     */
    int getManagedEntityCount();
}
