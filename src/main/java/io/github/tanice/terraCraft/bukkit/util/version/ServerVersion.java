package io.github.tanice.terraCraft.bukkit.util.version;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;

public class ServerVersion {
    private static final MCVersion MINECRAFT_VERSION = ServerVersion.getServerVersion();
    private static final boolean IS_PAPER;

    public static MCVersion get() {
        return MINECRAFT_VERSION;
    }

    public static boolean isPaper() {
        return IS_PAPER;
    }

    public static boolean isAfter(MCVersion other) {
        return MINECRAFT_VERSION.compareTo(other) > 0;
    }

    public static boolean isAfterOrEq(MCVersion other) {
        return MINECRAFT_VERSION.compareTo(other) >= 0;
    }

    public static boolean isBefore(MCVersion other) {
        return MINECRAFT_VERSION.compareTo(other) < 0;
    }

    public static boolean isBeforeOrEq(MCVersion other) {
        return MINECRAFT_VERSION.compareTo(other) <= 0;
    }

    public static boolean isBetween(MCVersion o1, MCVersion o2) {
        return ServerVersion.isAfterOrEq(o1) && ServerVersion.isBeforeOrEq(o2) || ServerVersion.isBeforeOrEq(o1) && ServerVersion.isAfterOrEq(o2);
    }

    public static boolean equals(MCVersion other) {
        return MINECRAFT_VERSION.getMajor() == other.getMajor() && MINECRAFT_VERSION.getMinor() == other.getMinor() && MINECRAFT_VERSION.getBuild() == other.getBuild();
    }

    public static boolean eq(MCVersion other) {
        return MINECRAFT_VERSION.getMajor() == other.getMajor() && MINECRAFT_VERSION.getMinor() == other.getMinor() && MINECRAFT_VERSION.getBuild() == other.getBuild();
    }

    private static MCVersion getServerVersion() {
        String versionString = Bukkit.getVersion();
        Matcher version = Pattern.compile(".*\\(.*MC.\\s*([a-zA-z0-9\\-\\.]+)\\s*\\)").matcher(versionString);
        if (version.matches() && version.group(1) != null) {
            return MCVersion.parse(version.group(1));
        }
        throw new IllegalStateException("Cannot parse version String '" + versionString + "'");
    }

    static {
        boolean paper = false;
        try {
            Class.forName("com.destroystokyo.paper.VersionHistoryManager$VersionData");
            paper = true;
        } catch (ClassNotFoundException ignored) {}
        IS_PAPER = paper;
    }
}