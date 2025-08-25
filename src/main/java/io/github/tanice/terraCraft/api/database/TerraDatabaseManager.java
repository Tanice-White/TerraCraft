package io.github.tanice.terraCraft.api.database;

import io.github.tanice.terraCraft.api.buff.TerraBuffRecord;
import io.github.tanice.terraCraft.api.player.TerraPlayerData;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface TerraDatabaseManager {

    void saveBuffRecords(Collection<TerraBuffRecord> records);

    CompletableFuture<List<TerraBuffRecord>> loadPlayerBuffRecords(String uuid);

    void savePlayerData(TerraPlayerData playerData);

    CompletableFuture<TerraPlayerData> loadPlayerData(String uuid);
}
