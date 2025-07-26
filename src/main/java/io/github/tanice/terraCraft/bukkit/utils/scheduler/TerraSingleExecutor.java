package io.github.tanice.terraCraft.bukkit.utils.scheduler;

public interface TerraSingleExecutor {
    void run(Runnable task);

    void shutdown();
}
