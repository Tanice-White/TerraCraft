package io.github.tanice.terraCraft.core.logger;

import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.utils.logger.ConsoleColor;

import java.util.logging.Level;

public final class TerraCraftLogger {
    private TerraCraftLogger() {}

    public static void success(String message) {
        TerraCraftBukkit.inst().getLogger().log(Level.INFO, ConsoleColor.GREEN + message + ConsoleColor.RESET);
    }

    public static void warning(String message) {
        TerraCraftLogger.log(Level.WARNING, message + ConsoleColor.RESET);
    }

    public static void error(String message) {
        TerraCraftLogger.log(Level.WARNING, ConsoleColor.RED + message + ConsoleColor.RESET);
    }

    public static void debug(DebugLevel level, String message) {
        TerraCraftLogger.log(Level.INFO, ConsoleColor.BLUE + "[" +  level.name() + "]" + message + ConsoleColor.RESET);
    }

    public static void log(String message) {
        TerraCraftBukkit.inst().getLogger().log(Level.INFO, message + ConsoleColor.RESET);
    }

    public static void log(String message, Object ... params) {
        TerraCraftBukkit.inst().getLogger().log(Level.INFO, message + ConsoleColor.RESET, params);
    }

    public static void log(Level level, String message) {
        TerraCraftBukkit.inst().getLogger().log(level, message + ConsoleColor.RESET);
    }

    public static void log(Level level, String message, Object ... params) {
        TerraCraftBukkit.inst().getLogger().log(level, message + ConsoleColor.RESET, params);
    }

    public enum DebugLevel {
        BUFF,
        BUFF_CONDITION,
        BUFF_TARGET,
        ITEM,
        PLAYER,
        SKILL,
        CALCULATOR
    }
}
