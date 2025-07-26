package io.github.tanice.terraCraft.bukkit.utils.scheduler;

import io.github.tanice.terraCraft.bukkit.utils.scheduler.task.TerraTask;

public interface TerraExecutor {

    void run(Runnable task);

    void runLater(Runnable task, long delayTicks);

    TerraTask repeat(Runnable task, long delayTicks, long periodTicks);

    void cancel(long taskId);

    void shutdown();
}