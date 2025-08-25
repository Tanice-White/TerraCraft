package io.github.tanice.terraCraft.bukkit.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MiniMessageUtil {
    /* 匹配闭合标签的正则(<gradient>...</gradient>) */
    private static final Pattern CLOSED_TAG_PATTERN = Pattern.compile("<([a-zA-Z]+)(:[^>]*)?>(.*?)</\\1>");
    /* 匹配单边标签的正则(<#ffffff>...) */
    private static final Pattern SINGLE_TAG_PATTERN = Pattern.compile("<(#\\\\w+)>([^<]*)");

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    /**
     * 将 String 打包成为 Component
     */
    public static Component serialize(@Nullable String message) {
        if (message == null || message.isEmpty()) return Component.empty();
        return miniMessage.deserialize(message);
    }

    /**
     * 解 Component 成 String
     */
    public static String deserialize (@Nullable Component component) {
        if (component == null) return "";
        return miniMessage.serialize(component);
    }

    /**
     * 将 component 化成 nbt json
     */
    public static String toNBTJson(Component component) {
        return GsonComponentSerializer.gson().serialize(component);
    }

    public static String stripAllTags(@Nullable String message) {
        if (message == null) return "";
        return miniMessage.stripTags(message);
    }

    public static String[] getOneLayerTags (String input) {
        Matcher closedMatcher = CLOSED_TAG_PATTERN.matcher(input);
        if (closedMatcher.find()) {
            String tagName = closedMatcher.group(1);// 标签名
            String params = closedMatcher.group(2);// 参数
            // 构建完整起始标签
            String startTag = "<" + tagName + (params != null ? params : "") + ">";
            String endTag = "</" + tagName + ">";
            return new String[]{startTag, endTag};
        }
        // 其次匹配单边标签
        Matcher singleMatcher = SINGLE_TAG_PATTERN.matcher(input);
        if (singleMatcher.find()) {
            String tag = singleMatcher.group(1);
            return new String[]{"<" + tag + ">", ""};
        }
        // 都不匹配时返回空
        return new String[]{"", ""};
    }
}
