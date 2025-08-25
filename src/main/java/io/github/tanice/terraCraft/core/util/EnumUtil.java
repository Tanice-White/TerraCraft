package io.github.tanice.terraCraft.core.util;

import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;

public final class EnumUtil {
    /**
     * 安全地将字符串转换为枚举值。如果转换失败（字符串无效或为null），则返回默认值。
     *
     * @param <T> 枚举类型
     * @param enumClass 枚举类的Class对象
     * @param value 要转换的字符串值
     * @param defaultValue 转换失败时的默认值
     * @return 转换后的枚举值，或默认值
     */
    public static <T extends Enum<T>> T safeValueOf(Class<T> enumClass, String value, T defaultValue) {
        if (value == null) return defaultValue;
        try {
            return Enum.valueOf(enumClass, value.toUpperCase());
        } catch (IllegalArgumentException e2) {
            TerraCraftLogger.warning(value + "' is not a valid value, use default: " + defaultValue);
            return defaultValue;
        }
    }
}
