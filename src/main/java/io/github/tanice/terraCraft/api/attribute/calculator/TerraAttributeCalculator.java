package io.github.tanice.terraCraft.api.attribute.calculator;

import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import io.github.tanice.terraCraft.api.buff.BuffActiveCondition;
import io.github.tanice.terraCraft.api.buff.TerraRunnableBuff;

import java.util.List;

public interface TerraAttributeCalculator {
    /**
     * 获取伤害前生效的buff
     * @return buff列表
     */
    List<TerraRunnableBuff> getOrderedBeforeList(BuffActiveCondition condition);

    /**
     * 获取防御计算前的生效buff
     * @return buff列表
     */
    List<TerraRunnableBuff> getOrderedBetweenList(BuffActiveCondition condition);

    /**
     * 获取最后生效的buff
     * @return buff列表
     */
    List<TerraRunnableBuff> getOrderedAfterList(BuffActiveCondition condition);

    /**
     * 获取玩家属性
     * @return 实体可计算属性类
     */
    TerraCalculableMeta getMeta();
}
