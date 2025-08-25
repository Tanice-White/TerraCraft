package io.github.tanice.terraCraft.bukkit.util.scheduler;

import java.util.concurrent.TimeUnit;

public final class Tick {
    public static final int TICKS_PER_SECOND = 20;
    public static final int MILLISECONDS_PER_SECOND = 1000;
    public static final int MILLISECONDS_PER_TICK = 50;

    public static long from(long duration, TimeUnit unit) {
        return unit.toMillis(duration) / 50L;
    }

    public static long to(long ticks, TimeUnit unit) {
        return unit.convert(ticks * 50L, TimeUnit.MILLISECONDS);
    }

    private Tick() {
    }
}
