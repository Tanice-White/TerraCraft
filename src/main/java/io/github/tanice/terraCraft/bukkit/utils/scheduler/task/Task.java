package io.github.tanice.terraCraft.bukkit.utils.scheduler.task;

import io.github.tanice.terraCraft.bukkit.utils.scheduler.Schedulers;

public record Task(long id) implements TerraTask {
    @Override
    public void cancel() {
        Schedulers.cancel(id);
    }
}
