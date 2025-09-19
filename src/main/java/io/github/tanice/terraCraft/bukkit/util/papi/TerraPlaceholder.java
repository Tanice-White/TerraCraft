package io.github.tanice.terraCraft.bukkit.util.papi;

import io.github.tanice.terraCraft.api.attribute.AttributeType;
import io.github.tanice.terraCraft.api.attribute.DamageFromType;
import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.util.nbtapi.NBTPlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class TerraPlaceholder extends PlaceholderExpansion {

    public TerraPlaceholder() {
    }

    @Override
    public @Nonnull String getIdentifier() {
        return TerraCraftBukkit.inst().getName().toLowerCase();
    }

    @Override
    public @Nonnull String getAuthor() {
        return String.join(", ", TerraCraftBukkit.inst().getDescription().getAuthors());
    }

    @Override
    public @Nonnull String getVersion() {
        return TerraCraftBukkit.inst().getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    /**
     * 支持的变量
     * mana
     * max_mana
     */
    @Override
    public String onRequest(OfflinePlayer offlinePlayer, @Nonnull String params) {
        // 确保玩家在线且有效
        Player player = offlinePlayer.getPlayer();
        if (player == null || !player.isOnline()) return "N/A";

        NBTPlayer nbtPlayer = NBTPlayer.from(player);
        TerraCalculableMeta meta = nbtPlayer.getMeta();
        // 处理基础属性
        switch (params.toLowerCase()) {
            case "external_health":
                return String.valueOf(nbtPlayer.getExternalHealth());
            case "mana":
                return String.valueOf(TerraCraftBukkit.inst().getSkillManager().getPlayerMana(player));
            case "max_mana":
                return String.valueOf(nbtPlayer.getMaxMana());
        }
        // attribute 类
        try {
            AttributeType type = AttributeType.valueOf(params.toUpperCase());
            return String.valueOf(meta.get(type));
        } catch (IllegalArgumentException ignored) {
        }
        // damage from type 类
        try {
            DamageFromType damageType = DamageFromType.valueOf(params.toUpperCase());
            return String.valueOf(meta.get(damageType));
        } catch (IllegalArgumentException ignored) {
        }
        return null;
    }
}
