package io.github.tanice.terraCraft.api.plugin;

import io.github.tanice.terraCraft.api.attribute.TerraEntityAttributeManager;
import io.github.tanice.terraCraft.api.buff.TerraBuffManager;
import io.github.tanice.terraCraft.api.item.TerraItemManager;
import io.github.tanice.terraCraft.api.skill.TerraSkillManager;
import io.github.tanice.terraCraft.api.database.TerraDatabaseManager;
import io.github.tanice.terraCraft.api.js.TerraJSEngineManager;

import java.io.File;

public interface TerraPlugin {

    void reload();

    TerraBuffManager getBuffManager();

    TerraItemManager getItemManager();

    TerraJSEngineManager getJSEngineManager();

    TerraDatabaseManager getDatabaseManager();

    TerraEntityAttributeManager getEntityAttributeManager();

    TerraSkillManager getSkillManager();

    File getDataFolder();
}
