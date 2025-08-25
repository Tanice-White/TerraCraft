package io.github.tanice.terraCraft.bukkit.util.event;

import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.EventPriority;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public final class EventSubscriptionBuilder<T extends Event> {
    private final Class<T> eventType;
    private final Class<? extends T>[] eventClasses;
    private EventPriority priority = EventPriority.NORMAL;
    private boolean ignoreCancelled = false;
    private Consumer<T> handler;

    @SafeVarargs
    private EventSubscriptionBuilder(Class<T> eventType, Class<? extends T>... eventClasses) {
        this.eventType = Objects.requireNonNull(eventType, "eventType cannot be null");
        this.eventClasses = Objects.requireNonNull(eventClasses, "eventClasses cannot be null");

        // 校验事件类不为空且类型匹配
        if (eventClasses.length == 0) {
            throw new IllegalArgumentException("At least one event class must be provided");
        }
        for (Class<? extends T> cls : eventClasses) {
            Objects.requireNonNull(cls, "event class cannot be null");
            if (!eventType.isAssignableFrom(cls)) {
                throw new IllegalArgumentException("Event class " + cls.getName() + " is not a subclass of " + eventType.getName());
            }
        }
    }

    /**
     * 创建构建器实例（支持单个/多个事件类）
     * @param eventType 事件父类型（统一处理器的参数类型）
     * @param eventClasses 要订阅的事件类
     */
    @SafeVarargs
    public static <T extends Event> EventSubscriptionBuilder<T> newBuilder(Class<T> eventType, Class<? extends T>... eventClasses) {
        return new EventSubscriptionBuilder<>(eventType, eventClasses);
    }

    /**
     * 设置事件优先级
     */
    public EventSubscriptionBuilder<T> priority(EventPriority priority) {
        this.priority = Objects.requireNonNull(priority, "priority cannot be null");
        return this;
    }

    /**
     * 设置是否忽略已取消的事件
     */
    public EventSubscriptionBuilder<T> ignoreCancelled(boolean ignoreCancelled) {
        this.ignoreCancelled = ignoreCancelled;
        return this;
    }

    /**
     * 设置事件处理器（统一处理所有订阅的事件）
     */
    public EventSubscriptionBuilder<T> handler(Consumer<T> handler) {
        this.handler = Objects.requireNonNull(handler, "handler cannot be null");
        return this;
    }

    /**
     * 注册所有事件订阅
     */
    public void register() {
        Objects.requireNonNull(handler, "handler must be set before registering");

        for (Class<? extends T> eventClass : eventClasses) {
            Listener listener = createListener(eventClass);
            if (!configureEventHandlerAnnotation(listener, eventClass)) continue;
            Bukkit.getPluginManager().registerEvents(listener, TerraCraftBukkit.inst());
        }
    }

    /**
     * 创建事件监听器（每个事件类对应一个监听器）
     */
    private Listener createListener(Class<? extends T> eventClass) {
        return new Listener() {
            @EventHandler(priority = EventPriority.NORMAL) // 临时默认值，后续会被替换
            public void onEvent(Event event) {
                try {
                    if (!eventClass.isInstance(event)) return;
                    T typedEvent = eventClass.cast(event);
                    // 过滤已取消的事件
                    if (ignoreCancelled && event instanceof Cancellable && ((Cancellable) event).isCancelled()) return;
                    // 确保事件类型匹配（防御性检查）
                    if (eventClass.isInstance(event)) handler.accept(typedEvent);

                } catch (Exception e) {
                    TerraCraftLogger.error("Error handling event " + eventClass.getSimpleName() + " " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
    }

    /**
     * 动态修改@EventHandler注解的属性（优先级和忽略取消事件）
     */
    private boolean configureEventHandlerAnnotation(Listener listener, Class<? extends T> eventClass) {
        try {
            Method method = listener.getClass().getDeclaredMethod("onEvent", Event.class);
            EventHandler annotation = method.getAnnotation(EventHandler.class);
            TerraCraftLogger.success(Arrays.toString(annotation.getClass().getDeclaredFields()));
            Field field = annotation.getClass().getDeclaredField("memberValues");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, Object> values = (Map<String, Object>) field.get(annotation);
            values.put("priority", priority);
            values.put("ignoreCancelled", ignoreCancelled);
            return true;
        } catch (Exception e) {
            TerraCraftLogger.error("Failed to configure event annotation for " + eventClass.getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
