package io.github.tanice.terraCraft.bukkit.utils.scheduler.task;

public interface TerraTask {
    /**
     * 获取任务唯一ID
     */
    long id();

    /**
     * 取消任务
     */
    void cancel();
}
