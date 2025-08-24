package io.github.tanice.terraCraft.api.buffs;

import io.github.tanice.terraCraft.bukkit.utils.TerraWeakReference;

public interface TerraBuffRecord {

    /**
     * 获取此记录对应的实体ID
     *
     * @return 实体ID
     */
    TerraWeakReference getEntityReference();

    /**
     * 获取此记录对应的buff实体引用
     * @return buff实体引用
     */
    TerraBaseBuff getBuff();

    /**
     * 获取此buff的下次生效计时
     * @return 距离下次生效的ticks
     */
    int getCooldownCounter();

    /**
     * 将buff距离下次生效的计时减去delta个tick
     * @param delta 需要减去的tick
     */
    void cooldown(int delta);

    /**
     * 刷新生效计时
     */
    void reloadCooldown();

    /**
     * 获取buff持续时间
     * @return buff的持续时间
     */
    int getDurationCounter();

    /**
     * 同种buff合并
     * @param other 新buff
     * @param isPermanent 新buff是否为永久buff
     */
    void merge(TerraBaseBuff other, boolean isPermanent);

    /**
     * 此记录的buff是否是永久型buff
     * @return 是否是永久型buff
     */
    boolean isPermanent();

    /**
     * 此记录的buff是否是周期生效buff
     * @return 是否是周期生效buff
     */
    boolean isTimer();

    /**
     * 此记录的buff是否是js定义的
     * @return 是否是js定义的
     */
    boolean isRunnable();
}
