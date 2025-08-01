package io.github.tanice.terraCraft.api.buffs;

public interface TerraBaseBuff extends Comparable<TerraBaseBuff> {

    /**
     * 获取buff内部名
     * @return buff内部名
     */
    String getName();

    /**
     * 获取buff的显示名
     * @return buff显示名
     */
    String getDisplayName();

    /**
     * 获取buff是否启用
     * @return buff是否启用
     */
    boolean enabled();

    /**
     * 获取buff激活概率
     * @return buff激活概率
     */
    double getChance();

    /**
     * 设置buff激活效率
     * @param chance 新的激活效率
     */
    void setChance(double chance);

    /**
     * 获取buff的持续ticks
     * @return buff的持续ticks
     */
    int getDuration();

    /**
     * 设置buff的持续ticks
     * @param duration 新的持续ticks
     */
    void setDuration(int duration);

    /**
     * 获取优先值
     */
    int getPriority();

    /**
     * 根据条件判断此buff是否生效
     * @param condition buff生效条件
     * @return 此buff是否生效
     */
    boolean isActiveUnder(BuffActiveCondition condition);

    /**
     * 克隆
     * @return 自身的可变副本
     */
    TerraBaseBuff clone();

    default int compareTo(TerraBaseBuff other) {
        return Integer.compare(this.getPriority(), other.getPriority());
    }
}
