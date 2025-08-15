package io.github.tanice.terraCraft.api.items.components;

import javax.annotation.Nullable;
import java.util.List;

public interface TerraBuffComponent extends TerraBaseComponent {

    /**
     * 获取持有时给予自身的Buff列表
     */
    @Nullable List<String> getHold();

    /**
     * 设置持有时给予自身的Buff列表
     */
    void setHold(@Nullable List<String> buffs);

    /**
     * 获取攻击时给予自身的Buff列表
     */
    @Nullable List<String> getAttackSelf();

    /**
     * 设置攻击时时给予自身的Buff列表
     */
    void setAttackSelf(@Nullable List<String> buffs);

    /**
     * 获取攻击目标时给予目标的Buff列表
     */
    @Nullable List<String> getAttack();

    /**
     * 设置攻击目标时给予对方的Buff列表
     */
    void setAttack(@Nullable List<String> buffs);

    /**
     * 获取防御时给予自身的Buff列表
     */
    @Nullable List<String> getDefenseSelf();

    /**
     * 设置自身防御时给予自身的Buff列表
     */
    void setDefenseSelf(@Nullable List<String> buffs);

    /**
     * 获取防御时给予对方的Buff列表
     */
    @Nullable List<String> getDefense();

    /**
     * 设置防御时给予目标的Buff列表
     */
    void setDefense(@Nullable List<String> buffs);
}
