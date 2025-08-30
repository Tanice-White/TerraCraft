package io.github.tanice.terraCraft.bukkit.util.event;

import io.github.tanice.terraCraft.bukkit.util.scheduler.TerraSchedulers;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;

import java.util.Objects;

public final class TerraEvents {
//
//    public static <T extends Event> EventSubscriptionBuilder<T> subscribe(Class<T> eventClass) {
//        Objects.requireNonNull(eventClass, "eventClass cannot be null");
//        return EventSubscriptionBuilder.newBuilder(eventClass, eventClass);
//    }
//
//    public static <T extends Event> EventSubscriptionBuilder<T> subscribe(Class<T> eventClass, EventPriority priority) {
//        return subscribe(eventClass).priority(priority);
//    }
//
//    @SafeVarargs
//    public static <T extends Event> EventSubscriptionBuilder<T> subscribe(Class<T> superType, Class<? extends T>... eventClasses) {
//        Objects.requireNonNull(superType, "superType cannot be null");
//        Objects.requireNonNull(eventClasses, "eventClasses cannot be null");
//        return EventSubscriptionBuilder.newBuilder(superType, eventClasses);
//    }
//
//    @SafeVarargs
//    public static <T extends Event> EventSubscriptionBuilder<T> subscribe(Class<T> superType, EventPriority priority, Class<? extends T>... eventClasses) {
//        return subscribe(superType, eventClasses).priority(priority);
//    }
//
//    @SafeVarargs
//    public static <T extends Event> EventSubscriptionBuilder<T> subscribe(TypeToken<T> type, Class<? extends T>... eventClasses) {
//        Objects.requireNonNull(type, "type cannot be null");
//        @SuppressWarnings("unchecked")
//        Class<T> superType = (Class<T>) type.getRawType();
//        return subscribe(superType, eventClasses);
//    }

    public static void call(Event event) {
        Objects.requireNonNull(event, "event cannot be null");
        Bukkit.getPluginManager().callEvent(event);
    }

    public static void callAsync(Event event) {
        Objects.requireNonNull(event, "event cannot be null");
        TerraSchedulers.async().run(() -> call(event));
    }

    public static void callSync(Event event) {
        Objects.requireNonNull(event, "event cannot be null");
        if (Bukkit.isPrimaryThread()) call(event);
        else TerraSchedulers.sync().run(() -> call(event));
    }

    public static <T extends Event> T callAndReturn(T event) {
        Objects.requireNonNull(event, "event cannot be null");
        call(event);
        return event;
    }

    private TerraEvents() {
        throw new UnsupportedOperationException("TerraEvents class cannot be instantiated");
    }
}