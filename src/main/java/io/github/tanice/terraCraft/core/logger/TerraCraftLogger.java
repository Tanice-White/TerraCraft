package io.github.tanice.terraCraft.core.logger;

import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.util.logger.ConsoleColor;

import java.util.logging.Level;

public final class TerraCraftLogger {
    private TerraCraftLogger() {}

    public static void success(String message) {
        TerraCraftBukkit.inst().getLogger().log(Level.INFO, ConsoleColor.GREEN + message + ConsoleColor.RESET);
    }

    public static void warning(String message) {
        TerraCraftLogger.info(Level.WARNING, message + ConsoleColor.RESET);
    }

    public static void error(String message) {
        TerraCraftLogger.info(Level.WARNING, ConsoleColor.RED + message + ConsoleColor.RESET);
    }

    public static void debug(DebugLevel level, String message) {
        TerraCraftLogger.info(Level.INFO, ConsoleColor.CYAN + "[" + ConsoleColor.YELLOW + level.name() + ConsoleColor.CYAN + "] " + message + ConsoleColor.RESET);
    }

    public static void info(String message) {
        TerraCraftBukkit.inst().getLogger().log(Level.INFO, message + ConsoleColor.RESET);
    }

    public static void info(String message, Object ... params) {
        TerraCraftBukkit.inst().getLogger().log(Level.INFO, message + ConsoleColor.RESET, params);
    }

    public static void info(Level level, String message) {
        TerraCraftBukkit.inst().getLogger().log(level, message + ConsoleColor.RESET);
    }

    public static void info(Level level, String message, Object ... params) {
        TerraCraftBukkit.inst().getLogger().log(level, message + ConsoleColor.RESET, params);
    }

    public enum DebugLevel {
        BUFF,
        ITEM,
        PLAYER,
        SKILL,
        CALCULATOR
    }
}
