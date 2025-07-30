package io.github.tanice.terraCraft.core.players;

import io.github.tanice.terraCraft.api.players.TerraPlayerData;
import io.github.tanice.terraCraft.api.players.TerraPlayerDataManager;
import io.github.tanice.terraCraft.api.utils.database.TerraDatabaseManager;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.events.entity.TerraPlayerDataLimitChangeEvent;
import io.github.tanice.terraCraft.bukkit.utils.events.TerraEvents;
import io.github.tanice.terraCraft.bukkit.utils.scheduler.TerraSchedulers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PlayerDataManager implements TerraPlayerDataManager {

    private static final int MANA_RECOVERY_CD = 2;
    private static final int CLEAN_UP_CD = 7;

    private final ConcurrentMap<UUID, TerraPlayerData> playerData;
    private final ConcurrentMap<UUID, Double> playerMana;

    public PlayerDataManager() {
        playerData = new ConcurrentHashMap<>();
        playerMana = new ConcurrentHashMap<>();

        TerraSchedulers.async().repeat(this::processManaRecovery, 2, MANA_RECOVERY_CD);
        TerraSchedulers.async().repeat(this::cleanup, 1, CLEAN_UP_CD);

        TerraEvents.subscribe(TerraPlayerDataLimitChangeEvent.class)
                .priority(EventPriority.MONITOR)
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
            // TODO 从数据库加载 同时初始化mana
        });
    }

    @Override
    public void changePlayerDataLimit(Player player, TerraPlayerData deltaPlayerData) {
        // TODO 自更新并写入数据库
    }

    private void processManaRecovery() {
        Iterator<Map.Entry<UUID, Double>> it = playerMana.entrySet().iterator();

        Entity e;
        Map.Entry<UUID, Double> entry;
        UUID uuid;
        TerraPlayerData data;
        double v;
        while(it.hasNext()) {
            entry = it.next();
            uuid = entry.getKey();
            e = Bukkit.getEntity(uuid);
            if (!(e instanceof Player) || !e.isValid()) continue;

            data = playerData.get(uuid);
            v = entry.getValue() + data.getManaRecoverySpeed();
            if (v < 0) v = 0;
            if (v > data.getMaxMana()) v = data.getMaxMana();
            playerMana.put(uuid, v);
        }
    }

    private void cleanup() {
        TerraDatabaseManager databaseManager = TerraCraftBukkit.inst().getDatabaseManager();
        Iterator<Map.Entry<UUID, Double>> it = playerMana.entrySet().iterator();

        Entity e;
        Map.Entry<UUID, Double> entry;
        UUID uuid;
        TerraPlayerData pd;
        while(it.hasNext()) {
            entry = it.next();
            uuid = entry.getKey();
            e = Bukkit.getEntity(uuid);
            if (e != null && e.isValid()) continue;

            pd = playerData.get(uuid);
            pd.setMana(playerMana.get(uuid));
            databaseManager.savePlayerData(pd);
            it.remove();
            playerData.remove(uuid);
        }
    }
}
