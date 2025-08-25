package io.github.tanice.terraCraft.bukkit.util.scheduler;

public interface TerraSingleExecutor {
    void run(Runnable task);

    void shutdown();
}
