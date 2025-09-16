package io.github.tanice.terraCraft.bukkit.command.attribute;

import io.github.tanice.terraCraft.api.attribute.AttributeActiveSection;
import io.github.tanice.terraCraft.api.attribute.AttributeType;
import io.github.tanice.terraCraft.api.attribute.DamageFromType;
import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.command.CommandRunner;
import io.github.tanice.terraCraft.bukkit.util.nbtapi.NBTPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static io.github.tanice.terraCraft.api.command.TerraCommand.*;
import static io.github.tanice.terraCraft.api.command.TerraCommand.GREEN;

public class PlayerAttributeSetCommand extends CommandRunner {

    private static final List<String> BASE_ATTRIBUTE = Arrays.asList("external_health", "mana", "max_mana", "section");
    private static final List<String> TYPE_ATTRIBUTE = Arrays.stream(AttributeType.values()).map(Enum::name).toList();
    private static final List<String> DAMAGE_ATTRIBUTE = Arrays.stream(DamageFromType.values()).map(Enum::name).toList();

    private static final List<String> SECTION = Arrays.stream(AttributeActiveSection.values()).map(Enum::name).toList();

    @Override
    public String getName() {
        return "set";
    }

    @Override
    public String getDescription() {
        return "set original attribute for the player";
    }

    @Override
    public String getUsage() {
        return """
                set <attribute_key> <value>
                set <attribute_key> <value> <player>
                """;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 2 || args.length > 3) {
            sender.sendMessage(RED + "Invalid number of arguments");
            return true;
        }

        String key = args[0];
        String valueStr = args[1];
        Player player = (args.length == 3) ? Bukkit.getPlayer(args[2]) : (sender instanceof Player ? (Player) sender : null);
        if (player == null) {
            sender.sendMessage(RED + "Invalid player name: " + args[0]);
            return true;
        }

        NBTPlayer nbtPlayer = NBTPlayer.from(player).clone();
        TerraCalculableMeta meta = nbtPlayer.getMeta();
        boolean success = false;

        // 处理基础属性
        if (BASE_ATTRIBUTE.contains(key)) {
            switch (key) {
                case "external_health":
                    try {
                        nbtPlayer.setExternalHealth(Float.parseFloat(valueStr));
                        success = true;
                    } catch (NumberFormatException e) {
                        sender.sendMessage(RED + "Invalid external_health value: " + valueStr);
                    }
                    break;
                case "mana":
                    try {
                        nbtPlayer.setMana(Double.parseDouble(valueStr));
                        success = true;
                    } catch (NumberFormatException e) {
                        sender.sendMessage(RED + "Invalid mana value: " + valueStr);
                    }
                    break;
                case "max_mana":
                    try {
                        nbtPlayer.setMaxMana(Double.parseDouble(valueStr));
                        success = true;
                    } catch (NumberFormatException e) {
                        sender.sendMessage(RED + "Invalid max_mana value: " + valueStr);
                    }
                    break;
                case "section":
                    try {
                        meta.setAttributeActiveSection(AttributeActiveSection.valueOf(valueStr.toUpperCase()));
                        nbtPlayer.setMeta(meta);
                        success = true;
                    } catch (IllegalArgumentException e) {
                        sender.sendMessage(RED + "Invalid section: " + valueStr);
                    }
                    break;
                default:
                    sender.sendMessage(RED + "Unknown base attribute: " + key);
            }
        }
        // 处理AttributeType枚举
        else if (TYPE_ATTRIBUTE.contains(key.toUpperCase(Locale.ENGLISH))) {
            try {
                AttributeType type = AttributeType.valueOf(key.toUpperCase());
                meta.set(type, Double.parseDouble(valueStr));
                nbtPlayer.setMeta(meta);
                success = true;
            } catch (IllegalArgumentException e) {
                sender.sendMessage(RED + "Invalid key-value in: " + key);
            }
        }
        // 处理DamageFromType枚举
        else if (DAMAGE_ATTRIBUTE.contains(key.toUpperCase(Locale.ENGLISH))) {
            try {
                DamageFromType type = DamageFromType.valueOf(key.toUpperCase());
                meta.set(type, Double.parseDouble(valueStr));
                nbtPlayer.setMeta(meta);
                success = true;
            } catch (IllegalArgumentException e) {
                sender.sendMessage(RED + "Invalid key-value in: " + key);
            }
        } else sender.sendMessage(RED + "Unknown key: " + key);

        // 应用修改并反馈
        if (success) {
            nbtPlayer.apply(player);
            /* 更新对应玩家的属性 */
            TerraCraftBukkit.inst().getEntityAttributeManager().updateAttribute(player);
            sender.sendMessage(GREEN + "Updated " + key + " to " + valueStr);
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ArrayList<String> mergedList = new ArrayList<>();
            mergedList.addAll(BASE_ATTRIBUTE);
            mergedList.addAll(TYPE_ATTRIBUTE);
            mergedList.addAll(DAMAGE_ATTRIBUTE);
            return mergedList;
        }
        if (args.length == 1 && args[0].equals("section")) return SECTION;
        if (args.length == 2) return playerList("");
        if (args.length == 3) return playerList(args[2]);
        return null;
    }
}
