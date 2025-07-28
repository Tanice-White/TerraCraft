package io.github.tanice.terraCraft.core.players;

import io.github.tanice.terraCraft.api.players.TerraPlayerData;
import io.github.tanice.terraCraft.api.players.TerraPlayerDataManager;
import io.github.tanice.terraCraft.api.service.TerraCacheService;
import io.github.tanice.terraCraft.api.service.TerraCached;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.events.entity.TerraPlayerDataLimitChangeEvent;
import io.github.tanice.terraCraft.bukkit.utils.events.TerraEvents;
import io.github.tanice.terraCraft.bukkit.utils.scheduler.TerraSchedulers;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PlayerDataManager implements TerraPlayerDataManager {

    private static final int MANA_RECOVERY_CD = 1;

    private final ConcurrentMap<UUID, TerraPlayerData> playerData;
    private final ConcurrentMap<UUID, Double> playerMana;

    public PlayerDataManager() {
        playerData = new ConcurrentHashMap<>();
        playerMana = new ConcurrentHashMap<>();

        TerraSchedulers.async().repeat(this::processManaRecovery, 2, MANA_RECOVERY_CD);

        TerraEvents.subscribe(TerraPlayerDataLimitChangeEvent.class)
                .priority(EventPriority.HIGH)
                .handler( event -> this.changePlayerDataLimit(event.getEntity(), event.getDeltaPlayerData()))
                .register();
    }

    public void reload() {
        // TODO 写入数据库
    }

    public void unload() {
        // TODO 写入数据库
    }

    @Override
    public Optional<TerraPlayerData> getPlayerData(UUID uuid) {
        TerraPlayerData data = playerData.get(uuid);
        if (data == null) return Optional.empty();
        else return Optional.of(data.clone());
    }

    @Override
    public Optional<Double> getPlayerMana(UUID uuid) {
        return Optional.ofNullable(playerMana.get(uuid));
    }

    @Override
    public void loadPlayerData(UUID uuid) {
        TerraCraftBukkit.inst().getDatabaseManager().loadPlayerData(uuid.toString()).thenAccept(playerData -> {
            // TODO
        });
    }

    @Override
    public void changePlayerDataLimit(Player player, TerraPlayerData deltaPlayerData) {
        // TODO 写入数据库
    }

    private void processManaRecovery() {
        TerraCacheService cacheService = TerraCraftBukkit.inst().getCacheService();
        Iterator<Map.Entry<UUID, Double>> it = playerMana.entrySet().iterator();

        TerraCached cached;
        Map.Entry<UUID, Double> entry;
        UUID uuid;
        TerraPlayerData data;
        double v;
        while(it.hasNext()) {
            entry = it.next();
            uuid = entry.getKey();
            cached = cacheService.get(uuid);
            if (cached == null) {
                it.remove();
                playerData.remove(uuid);
                return;
            }
            data = playerData.get(uuid);
            v = entry.getValue() + data.getManaRecoverySpeed();
            if (v < 0) v = 0;
            if (v > data.getMaxMana()) v = data.getMaxMana();
            playerMana.put(uuid, v);
        }
    }
}
