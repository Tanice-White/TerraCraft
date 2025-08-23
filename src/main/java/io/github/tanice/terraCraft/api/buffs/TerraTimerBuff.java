package io.github.tanice.terraCraft.api.buffs;

public interface TerraTimerBuff extends TerraRunnableBuff{

    /**
     * 获取此buff效果的激活间隔
     * @return 间隔ticks
     */
    int getCd();

    void setCd(int cd);
}
