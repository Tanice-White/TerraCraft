package io.github.tanice.terraCraft.bukkit.util.scheduler;

import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.util.scheduler.task.Task;
import io.github.tanice.terraCraft.bukkit.util.scheduler.task.TerraTask;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public final class TerraSchedulers {
    private static final TerraExecutor syncExecutor = new SyncExecutor();
    private static final TerraExecutor asyncExecutor = new AsyncExecutor();
    private static final TerraSingleExecutor databaseAsyncExecutor = new SingleAsyncExecutor();

    private static final AtomicLong ids = new AtomicLong(0);
    private static final AtomicBoolean closing = new AtomicBoolean(false);

    public static TerraExecutor sync() {
        return syncExecutor;
    }

    public static TerraExecutor async() {
        return asyncExecutor;
    }

    public static TerraSingleExecutor databaseAsync() {
        return databaseAsyncExecutor;
    }

    public static void cancel(long taskId) {
        syncExecutor.cancel(taskId);
        asyncExecutor.cancel(taskId);
    }

    public static void shutdown() {
        syncExecutor.shutdown();
        databaseAsyncExecutor.shutdown();
        asyncExecutor.shutdown();
    }

    public static void clear() {
        syncExecutor.clear();
        asyncExecutor.clear();
    }

    private static void stateCheck() {
        if (closing.get()) throw new IllegalStateException("TerraSyncExecutor is shutting down");
    }

    private TerraSchedulers() {
        throw new UnsupportedOperationException("Schedulers class cannot be instantiated");
    }

    /**
     * 同步执行器
     */
    private static class SyncExecutor implements TerraExecutor {
        private final Map<Long, Integer> tasks;

        public SyncExecutor() {
            tasks = new ConcurrentHashMap<>();
        }

        @Override
        public void run(Runnable task) {
            TerraSchedulers.stateCheck();

            BukkitTask bukkitTask = Bukkit.getScheduler().runTask(
                    TerraCraftBukkit.inst(),
                    task
            );
            tasks.put(ids.getAndAdd(1), bukkitTask.getTaskId());
        }

        @Override
        public void runLater(Runnable task, long delayTicks) {
            TerraSchedulers.stateCheck();

            BukkitTask bukkitTask = Bukkit.getScheduler().runTaskLater(
                    TerraCraftBukkit.inst(),
                    task,
                    delayTicks
            );
            tasks.put(ids.getAndAdd(1), bukkitTask.getTaskId());
        }

        @Override
        public TerraTask repeat(Runnable task, long delayTicks, long periodTicks) {
            TerraSchedulers.stateCheck();

            BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimer(
                    TerraCraftBukkit.inst(),
                    task,
                    delayTicks,
                    periodTicks
            );
            long id = ids.getAndAdd(1);
            tasks.put(id, bukkitTask.getTaskId());
            return new Task(id);
        }

        @Override
        public void cancel(long taskId) {
            Integer bukkitTaskId = tasks.get(taskId);
            if (bukkitTaskId == null) return;

            Bukkit.getScheduler().cancelTask(bukkitTaskId);
            tasks.remove(taskId);
        }

        @Override
        public void shutdown() {
            closing.set(true);
            for (int bid : tasks.values()) Bukkit.getScheduler().cancelTask(bid);
            tasks.clear();
        }

        @Override
        public void clear() {
            for (int bid : tasks.values()) Bukkit.getScheduler().cancelTask(bid);
            tasks.clear();
        }
    }

    /**
     * 异步执行器
     */
    private static class AsyncExecutor implements TerraExecutor {
        private final ScheduledThreadPoolExecutor executor;
        private final Map<Long, Future<?>> tasks;

        public AsyncExecutor() {
            ThreadFactory threadFactory = new ThreadFactory() {
                private final AtomicInteger threadCount = new AtomicInteger(0);

                @Override
                public Thread newThread(@NotNull Runnable r) {
                    Thread thread = new Thread(r, "TerraCraftAsyncPool-" + threadCount.incrementAndGet());
                    thread.setDaemon(true);
                    thread.setUncaughtExceptionHandler((t, e) ->
                            TerraCraftBukkit.inst().getLogger().severe("Async task error: " + e.getMessage())
                    );
                    return thread;
                }
            };

            int coreThreads = Math.max(2, Runtime.getRuntime().availableProcessors());
            this.executor = new ScheduledThreadPoolExecutor(
                    coreThreads,
                    threadFactory
            );
            this.executor.setKeepAliveTime(30, TimeUnit.SECONDS);
            this.executor.allowCoreThreadTimeOut(true);

            tasks = new ConcurrentHashMap<>();
        }

        @Override
        public void run(Runnable task) {
            TerraSchedulers.stateCheck();

            tasks.put(ids.getAndAdd(1), this.executor.submit(task));
        }

        @Override
        public void runLater(Runnable task, long delayTicks) {
            TerraSchedulers.stateCheck();

            ScheduledFuture<?> future = this.executor.schedule(
                    task,
                    Tick.to(delayTicks, TimeUnit.MILLISECONDS),
                    TimeUnit.MILLISECONDS
            );
            tasks.put(ids.getAndAdd(1), future);
        }

        @Override
        public TerraTask repeat(Runnable task, long delayTicks, long periodTicks) {
            TerraSchedulers.stateCheck();

            ScheduledFuture<?> future = this.executor.scheduleAtFixedRate(
                    task,
                    Tick.to(delayTicks, TimeUnit.MILLISECONDS),
                    Tick.to(periodTicks, TimeUnit.MILLISECONDS),
                    TimeUnit.MILLISECONDS
            );
            long id = ids.getAndAdd(1);
            tasks.put(id, future);
            return new Task(id);
        }

        @Override
        public void cancel(long taskId) {
            Future<?> future = tasks.get(taskId);
            if (future == null) return;

            future.cancel(true);
            tasks.remove(taskId);
        }

        @Override
        public void shutdown() {
            closing.set(true);
            for (Future<?> future : tasks.values()) future.cancel(true);
            tasks.clear();

            this.executor.shutdown();
            try {
                if (!this.executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    this.executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                this.executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        @Override
        public void clear() {
            for (Future<?> future : tasks.values()) future.cancel(true);
            tasks.clear();
        }
    }

    /**
     * 数据库专用线程
     */
    private static class SingleAsyncExecutor implements TerraSingleExecutor {
        private final ExecutorService executor;

        public SingleAsyncExecutor() {
            this.executor = Executors.newSingleThreadExecutor(r -> {
                Thread t = new Thread(r);
                t.setName("TerraCraftAsync-single-Thread");
                t.setDaemon(true);
                return t;
            });
        }

        @Override
        public void run(Runnable task) {
            TerraSchedulers.stateCheck();
            executor.execute(task);
        }

        @Override
        public void shutdown() {
            this.executor.shutdown();
            try {
                if (!this.executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    this.executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                this.executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
