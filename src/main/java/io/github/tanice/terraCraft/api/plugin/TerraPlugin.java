package io.github.tanice.terraCraft.api.plugin;

import io.github.tanice.terraCraft.api.attribute.TerraEntityAttributeManager;
import io.github.tanice.terraCraft.api.buffs.TerraBuffManager;
import io.github.tanice.terraCraft.api.config.TerraConfigManager;
import io.github.tanice.terraCraft.api.items.TerraItemManager;
import io.github.tanice.terraCraft.api.players.TerraPlayerDataManager;
import io.github.tanice.terraCraft.api.service.TerraCacheService;
import io.github.tanice.terraCraft.api.skills.TerraSkillManager;
import io.github.tanice.terraCraft.api.utils.database.TerraDatabaseManager;
import io.github.tanice.terraCraft.api.utils.js.TerraJSEngineManager;

import java.io.File;

public interface TerraPlugin {

    void reload();

    TerraBuffManager getBuffManager();

    TerraConfigManager getConfigManager();

    TerraItemManager getItemManager();

    TerraJSEngineManager getJSEngineManager();

    TerraCacheService getCacheService();

    TerraDatabaseManager getDatabaseManager();

    TerraEntityAttributeManager getEntityAttributeManager();

    TerraSkillManager getSkillManager();

    TerraPlayerDataManager getPlayerDataManager();

    File getDataFolder();
}
