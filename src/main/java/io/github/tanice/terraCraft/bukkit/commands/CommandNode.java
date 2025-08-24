package io.github.tanice.terraCraft.bukkit.commands;

import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public abstract class CommandNode {

    public abstract String getName();

    public abstract boolean execute(CommandSender sender, String[] args);

    public String getDescription() {
        return "";
    }

    public String getUsage() {
        return "";
    }

    public String getPermission() {
        return "terracraft.command." + getName().toLowerCase();
    }

    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    public void onload(){}

    public void unload(){}

    public void reload(){}

    protected boolean hasPermission(CommandSender sender) {
        return sender.hasPermission(getPermission());
    }
}
