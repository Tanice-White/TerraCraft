package io.github.tanice.terraCraft.bukkit.commands;

import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.stream.Collectors;

import static io.github.tanice.terraCraft.api.commands.TerraCommand.*;

public abstract class GroupCommand extends SubCommand {
    protected final Map<String, SubCommand> subCommands = new HashMap<>();

    public GroupCommand() {
        register();
    }

    /**
     * 注册子命令，由子类实现
     */
    protected abstract void register();

    /**
     * 添加子命令
     */
    protected void addSubCommand(SubCommand command) {
        subCommands.put(command.getName().toLowerCase(), command);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!hasPermission(sender)) {
            sender.sendMessage(RED + "你没有权限使用此命令: " + getPermission());
            return true;
        }

        if (args.length == 0) {
            sendSubCommandsHelp(sender);
            return true;
        }

        SubCommand subCommand = subCommands.get(args[0].toLowerCase());
        if (subCommand == null) {
            sender.sendMessage(RED + "未知的子命令: " + args[0]);
            sendAvailableSubCommands(sender);
            return true;
        }

        if (!subCommand.hasPermission(sender)) {
            sender.sendMessage(RED + "你没有权限使用子命令: " + subCommand.getName());
            sender.sendMessage(RED + "所需权限: " + YELLOW + subCommand.getPermission());
            return true;
        }

        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        return subCommand.execute(sender, subArgs);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (!hasPermission(sender)) return Collections.emptyList();

        if (args.length == 1) {
            return subCommands.values().stream()
                    .filter(cmd -> cmd.hasPermission(sender))
                    .map(SubCommand::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .sorted()
                    .collect(Collectors.toList());
        }

        if (args.length > 1) {
            SubCommand subCommand = subCommands.get(args[0].toLowerCase());
            if (subCommand != null && subCommand.hasPermission(sender)) {
                String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
                return subCommand.tabComplete(sender, subArgs);
            }
        }

        return Collections.emptyList();
    }

    /**
     * 发送子命令帮助信息
     */
    protected void sendSubCommandsHelp(CommandSender sender) {
        sender.sendMessage(GOLD + "=== " + getName() + " 命令帮助 ===");
        subCommands.values().forEach(cmd -> {
            if (cmd.hasPermission(sender)) {
                sender.sendMessage(String.format(GRAY + "/%s %s %s §f- %s", getName(), cmd.getName(), cmd.getUsage(), cmd.getDescription()
                ));
            }
        });
    }

    /**
     * 发送可用的子命令列表
     */
    private void sendAvailableSubCommands(CommandSender sender) {
        List<String> availableCommands = subCommands.values().stream()
                .filter(cmd -> cmd.hasPermission(sender))
                .map(SubCommand::getName)
                .sorted()
                .collect(Collectors.toList());

        if (availableCommands.isEmpty()) {
            sender.sendMessage(RED + "你没有可用的子命令权限");
        } else {
            sender.sendMessage(GREEN + "可用子命令: " + String.join(GRAY + ", " + GREEN, availableCommands));
        }
    }

    @Override
    public String getUsage() {
        return "<子命令> [参数]";
    }
}
