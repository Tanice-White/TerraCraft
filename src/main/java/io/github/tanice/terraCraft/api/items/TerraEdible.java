package io.github.tanice.terraCraft.api.items;

import org.bukkit.entity.Player;

public interface TerraEdible extends TerraBaseItem {

    /**
     * 获取食用间隔tick
     * @return 间隔tick
     */
    int getCd();

    /**
     * 获取可食用次数
     * @return 次数限制
     */
    int getTimes();

    /**
     * 让可食用物品属性生效
     * @param player 食用它的玩家
     * @return 返回是否生效成功
     */
    boolean apply(Player player);
}
