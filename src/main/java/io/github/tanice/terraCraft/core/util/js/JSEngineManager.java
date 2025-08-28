package io.github.tanice.terraCraft.core.util.js;

import io.github.tanice.terraCraft.api.js.TerraJSEngineManager;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public final class JSEngineManager implements TerraJSEngineManager, AutoCloseable {
    private Context jsContext;
    private Map<String, Value> functionCache;
    private AtomicBoolean closed;

    private static final String FUNCTION_NAME = "run";

    public JSEngineManager() {
        reload();
    }

    public void reload() {
        functionCache = new ConcurrentHashMap<>();
        closed = new AtomicBoolean(false);

        this.jsContext = Context.newBuilder("js")
                .allowAllAccess(true)
                .option("engine.WarnInterpreterOnly", "false")
                .hostClassLoader(getClass().getClassLoader())
                .allowExperimentalOptions(true)
                .option("js.esm-eval-returns-exports", "true") // 启用ES模块支持
                .build();

        // 注册全局Java对象
        Value bindings = jsContext.getBindings("js");
        bindings.putMember("Player", org.bukkit.entity.Player.class);
        bindings.putMember("LivingEntity", org.bukkit.entity.LivingEntity.class);
        bindings.putMember("Entity", org.bukkit.entity.Entity.class);
        bindings.putMember("Location", org.bukkit.Location.class);
    }

    public void unload() {
        closed.set(true);
        functionCache.clear();
        jsContext.close();
    }

    @Override
    public void registerJavaObject(String objectName, Object object) {
        checkClosed();
        jsContext.getBindings("js").putMember(objectName, object);
    }

    /**
     * 加载JS脚本并提取关键变量
     * @param jsFileName 文件名
     * @param path 带文件名的可识别路径
     * @return js文件中导出的关键词的Map
     */
    @Override
    public Map<String, String> loadScript(String jsFileName, Path path) {
        checkClosed();
        try {
            Value exports = jsContext.eval(
                    Source.newBuilder("js", path.toFile())
                            .mimeType("application/javascript+module")
                            .build()
            );
            /* 缓存函数 */
            Value runFunction = exports.getMember(FUNCTION_NAME);
            if (runFunction != null && runFunction.canExecute()) functionCache.put(jsFileName, runFunction);
            return extractVariables(exports);

        } catch (Exception e) {
            TerraCraftLogger.warning("Failed to load script: " + jsFileName + " " + e.getMessage());
            return Collections.emptyMap();
        }
    }

    // 执行JS中的run函数
    @Override
    public boolean executeFunction(String jsFileName, Object... args) {
        checkClosed();
        Value function = functionCache.get(jsFileName);
        if (function == null) {
            TerraCraftLogger.warning("Function not loaded: " + jsFileName);
            return true;
        }
        return function.execute(args).asBoolean();
    }

    private Map<String, String> extractVariables(Value exports) {
        Set<String> exportNames = exports.getMemberKeys();
        Map<String, String> result = new HashMap<>();
        for (String key : exportNames) {
            Value value = exports.getMember(key);
            if (value != null && !value.isNull() && !value.canExecute()) {
                result.put(key, valueToString(value));
            }
        }
        return result;
    }

    // 类型安全的Value转换
    private String valueToString(Value value) {
        if (value.isString()) return value.asString();
        if (value.isNumber()) return String.valueOf(value.asDouble());
        if (value.isBoolean()) return String.valueOf(value.asBoolean());
        if (value.hasArrayElements()) return arrayToString(value);
        return value.toString();
    }

    private String arrayToString(Value array) {
        StringBuilder sb = new StringBuilder("[");
        long size = array.getArraySize();
        for (long i = 0; i < size; i++) {
            if (i > 0) sb.append(",");
            sb.append(array.getArrayElement(i).toString());
        }
        return sb.append("]").toString();
    }

    private void checkClosed() {
        if (closed.get()) throw new IllegalStateException("TerraJSEngineManager has been closed");
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            if (jsContext != null) jsContext.close();
            functionCache.clear();
        }
    }
}
