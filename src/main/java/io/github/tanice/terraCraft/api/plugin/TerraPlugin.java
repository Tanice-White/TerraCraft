package io.github.tanice.terraCraft.api.plugin;

import io.github.tanice.terraCraft.api.attribute.TerraEntityAttributeManager;
import io.github.tanice.terraCraft.api.buffs.TerraBuffManager;
import io.github.tanice.terraCraft.api.config.TerraConfigManager;
import io.github.tanice.terraCraft.api.items.TerraItemManager;
import io.github.tanice.terraCraft.api.service.TerraCacheService;
import io.github.tanice.terraCraft.api.utils.database.TerraDatabaseManager;
import io.github.tanice.terraCraft.api.utils.js.TerraJSEngineManager;

import java.io.File;

public interface TerraPlugin {

    TerraBuffManager getBuffManager();

    TerraConfigManager getConfigManager();

    TerraItemManager getItemManager();

    TerraJSEngineManager getJSEngineManager();

    TerraCacheService getCacheService();

    TerraDatabaseManager getDatabaseManager();

    TerraEntityAttributeManager getEntityAttributeManager();

    File getDataFolder();

    long getUpdateCode();

    void reload();
}
