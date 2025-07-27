package io.github.tanice.terraCraft.bukkit;

import io.github.tanice.terraCraft.api.attribute.TerraEntityAttributeManager;
import io.github.tanice.terraCraft.api.buffs.TerraBuffManager;
import io.github.tanice.terraCraft.api.config.TerraConfigManager;
import io.github.tanice.terraCraft.api.items.TerraItemManager;
import io.github.tanice.terraCraft.api.players.TerraPlayerDataManager;
import io.github.tanice.terraCraft.api.plugin.TerraPlugin;
import io.github.tanice.terraCraft.api.service.TerraCacheService;
import io.github.tanice.terraCraft.api.skills.TerraSkillManager;
import io.github.tanice.terraCraft.api.utils.database.TerraDatabaseManager;
import io.github.tanice.terraCraft.api.utils.js.TerraJSEngineManager;
import io.github.tanice.terraCraft.bukkit.utils.scheduler.TerraSchedulers;
import io.github.tanice.terraCraft.core.attribute.EntityAttributeManager;
import io.github.tanice.terraCraft.core.buffs.BuffManager;
import io.github.tanice.terraCraft.core.config.ConfigManager;
import io.github.tanice.terraCraft.core.items.ItemManager;
import io.github.tanice.terraCraft.core.players.PlayerDataManager;
import io.github.tanice.terraCraft.core.service.EntityCacheService;
import io.github.tanice.terraCraft.core.skills.SkillManager;
import io.github.tanice.terraCraft.core.utils.database.DatabaseManager;
import io.github.tanice.terraCraft.core.utils.js.JSEngineManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class TerraCraftBukkit extends JavaPlugin implements TerraPlugin {
    private long updateCode;
    private static TerraCraftBukkit instance;

    private ConfigManager configManager;
    private JSEngineManager jsEngineManager;
    private DatabaseManager databaseManager;
    private BuffManager buffManager;
    private ItemManager itemManager;

    private EntityCacheService cacheService;
    private EntityAttributeManager entityAttributeManager;
    private SkillManager skillManager;
    private PlayerDataManager playerDataManager;

    @Override
    public void onEnable() {
        updateCode = System.currentTimeMillis();
        instance = this;

        jsEngineManager = new JSEngineManager();
        configManager = new ConfigManager(this);
        databaseManager = new DatabaseManager(this);
        buffManager = new BuffManager(this);
        itemManager = new ItemManager(this);
        skillManager = new SkillManager(this);

        cacheService = new EntityCacheService();

        entityAttributeManager = new EntityAttributeManager();
        playerDataManager = new PlayerDataManager();
    }

    @Override
    public void onDisable() {
        jsEngineManager.close();
        configManager.unload();
        buffManager.unload();
        itemManager.unload();
        skillManager.unload();
        entityAttributeManager.unload();
        playerDataManager.unload();
        TerraSchedulers.shutdown();
        databaseManager.unload();
        cacheService.unload();
    }

    @Override
    public void reload() {
        this.updateCode = System.currentTimeMillis();
        configManager.reload();
        databaseManager.reload();
        cacheService.reload();

        jsEngineManager.reload();
        buffManager.reload();
        itemManager.reload();
        skillManager.reload();
        entityAttributeManager.reload();
        playerDataManager.reload();
    }

    public static TerraCraftBukkit inst() {
        return instance;
    }

    @Override
    public TerraBuffManager getBuffManager() {
        return this.buffManager;
    }

    @Override
    public TerraConfigManager getConfigManager() {
        return this.configManager;
    }

    @Override
    public TerraItemManager getItemManager() {
        return this.itemManager;
    }

    @Override
    public TerraJSEngineManager getJSEngineManager() {
        return this.jsEngineManager;
    }

    @Override
    public TerraCacheService getCacheService() {
        return this.cacheService;
    }

    @Override
    public TerraDatabaseManager getDatabaseManager() {
        return this.databaseManager;
    }

    @Override
    public TerraEntityAttributeManager getEntityAttributeManager() {
        return this.entityAttributeManager;
    }

    @Override
    public TerraSkillManager getSkillManager() {
        return this.skillManager;
    }

    @Override
    public TerraPlayerDataManager getPlayerDataManager() {
        return this.playerDataManager;
    }

    @Override
    public long getUpdateCode() {
        return this.updateCode;
    }
}
