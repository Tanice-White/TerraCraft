package io.github.tanice.terraCraft.bukkit.utils.versions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;

public final class MinecraftVersions {
    public static final MCVersion v1_22 = MCVersion.parse("1.22");
    public static final MCVersion v1_21_7 = MCVersion.parse("1.21.7");
    public static final MCVersion v1_21_6 = MCVersion.parse("1.21.6");
    public static final MCVersion v1_21_5 = MCVersion.parse("1.21.5");
    public static final MCVersion v1_21_4 = MCVersion.parse("1.21.4");
    public static final MCVersion v1_21_3 = MCVersion.parse("1.21.3");
    public static final MCVersion v1_21_2 = MCVersion.parse("1.21.2");
    public static final MCVersion v1_21_1 = MCVersion.parse("1.21.1");
    public static final MCVersion v1_21 = MCVersion.parse("1.21");
    public static final MCVersion v1_20_6 = MCVersion.parse("1.20.6");
    public static final MCVersion v1_20_5 = MCVersion.parse("1.20.5");
    public static final MCVersion v1_20_4 = MCVersion.parse("1.20.4");
    public static final MCVersion v1_20_3 = MCVersion.parse("1.20.3");
    public static final MCVersion v1_20_2 = MCVersion.parse("1.20.2");
    public static final MCVersion v1_20_1 = MCVersion.parse("1.20.1");
    public static final MCVersion v1_20 = MCVersion.parse("1.20");

    private static final Pattern VERSION_PATTERN = Pattern.compile(".*\\(.*MC.\\s*([a-zA-z0-9\\-.]+)\\s*\\)");
    static final MCVersion RUNTIME_VERSION = MinecraftVersions.parseServerVersion(Bukkit.getVersion());

    private static MCVersion parseServerVersion(String serverVersion) {
        Matcher version = VERSION_PATTERN.matcher(serverVersion);
        if (version.matches() && version.group(1) != null) {
            return MCVersion.parse(version.group(1));
        }
        throw new IllegalStateException("Cannot parse version String '" + serverVersion + "'");
    }

    private MinecraftVersions() {
    }
}

