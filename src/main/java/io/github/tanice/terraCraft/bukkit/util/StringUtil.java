package io.github.tanice.terraCraft.bukkit.util;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class StringUtil {
    /**
     * 将输入字符串按逗号分割为字符串列表，自动处理前后空格
     */
    public static @Nonnull List<String> splitByComma(String input) {
        return input == null || input.trim().isEmpty() ? List.of() : Arrays.stream(input.trim().split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }
}
