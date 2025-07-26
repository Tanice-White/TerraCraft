package io.github.tanice.terraCraft.api.players;

import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public interface TerraPlayerDataManager {

    Optional<TerraPlayerData> getPlayerData(UUID uuid);

    Optional<Double> getPlayerMana(UUID uuid);

    void loadPlayerData(UUID uuid);

    void changePlayerDataLimit(Player player, TerraPlayerData playerData);
}
