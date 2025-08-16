package io.github.tanice.terraCraft.core.utils;

import javax.annotation.Nullable;

public class ColorUtil {
    /**
     * 将字符串转换为RGB整数
     * @param colorStr 输入字符串（支持整数形式或16进制RGB形式）
     * @return 对应的RGB整数（0-16777215范围）
     * @throws IllegalArgumentException 当输入字符串格式无效时抛出
     */
    @Nullable
    public static Integer stringToRgb(@Nullable String colorStr) {
        if (colorStr == null) return null;
        String trimmed = colorStr.trim();
        try {
            int intValue = Integer.parseInt(trimmed);
            if (intValue >= 0 && intValue <= 0xFFFFFF) return intValue;
            else throw new IllegalArgumentException("Value out of range (0-16777215): " + intValue);
        } catch (NumberFormatException ignore) {
        }

        String hexStr = trimmed.startsWith("#") ? trimmed.substring(1) : trimmed;
        if (hexStr.length() != 6) throw new IllegalArgumentException("Hex RGB string must be 6 characters (or 7 with #): " + colorStr);
        try {
            int hexValue = Integer.parseInt(hexStr, 16);
            return hexValue & 0xFFFFFF;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hexadecimal RGB string: " + colorStr, e);
        }
    }
}
