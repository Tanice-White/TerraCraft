package io.github.tanice.terraCraft.bukkit.util.logger;

// TODO 换配色
public enum ConsoleColor {
    BLACK("\u001b[30m"),
    RED("\u001b[31m"),
    GREEN("\u001b[32m"),
    YELLOW("\u001b[33m"),
    BLUE("\u001b[34m"),
    PURPLE("\u001b[35m"),
    CYAN("\u001b[36m"),
    WHITE("\u001b[37m"),
    RESET("\u001b[0m"),
    BOLD("\u001b[1m"),
    ITALICS("\u001b[2m"),
    UNDERLINE("\u001b[4m"),
    CHECK_MARK("\u2713"),
    ERROR_MARK("\u2717");

    private final String ansiColor;

    ConsoleColor(String ansiColor) {
        this.ansiColor = ansiColor;
    }

    public String getAnsiColor() {
        return this.ansiColor;
    }

    public String toString() {
        return this.ansiColor;
    }
}
