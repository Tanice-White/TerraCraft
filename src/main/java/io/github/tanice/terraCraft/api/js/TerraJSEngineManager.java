package io.github.tanice.terraCraft.api.js;

import java.nio.file.Path;
import java.util.Map;

public interface TerraJSEngineManager {

    Map<String, String> loadScript(String jsFileName, Path path);

    void registerJavaObject(String objectName, Object object);

    boolean executeFunction(String jsFileName, Object... args);
}
