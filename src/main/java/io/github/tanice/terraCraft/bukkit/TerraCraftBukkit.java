package io.github.tanice.terraCraft.bukkit;

import io.github.tanice.terraCraft.api.attribute.TerraEntityAttributeManager;
import io.github.tanice.terraCraft.api.buffs.TerraBuffManager;
import io.github.tanice.terraCraft.api.config.TerraConfigManager;
import io.github.tanice.terraCraft.api.items.TerraItemManager;
import io.github.tanice.terraCraft.api.plugin.TerraPlugin;
import io.github.tanice.terraCraft.api.service.TerraCacheService;
import io.github.tanice.terraCraft.api.utils.database.TerraDatabaseManager;
import io.github.tanice.terraCraft.api.utils.js.TerraJSEngineManager;
import io.github.tanice.terraCraft.bukkit.utils.scheduler.Schedulers;
import io.github.tanice.terraCraft.core.attribute.EntityAttributeManager;
import io.github.tanice.terraCraft.core.buffs.BuffManager;
import io.github.tanice.terraCraft.core.config.ConfigManager;
import io.github.tanice.terraCraft.core.items.ItemManager;
import io.github.tanice.terraCraft.core.service.EntityCacheService;
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

    private EntityAttributeManager entityAttributeManager;
    private TerraCacheService cacheService;

    @Override
    public void onEnable() {
        updateCode = System.currentTimeMillis();
        instance = this;

        configManager = new ConfigManager(this);
        jsEngineManager = new JSEngineManager();
        databaseManager = new DatabaseManager(this);

        buffManager = new BuffManager(this);
        itemManager = new ItemManager(this);

        entityAttributeManager = new EntityAttributeManager();
        cacheService = new EntityCacheService();
        // TODO 全局异步缓存清理
    }

    @Override
    public void onDisable() {
        configManager.unload();
        buffManager.unload();
        Schedulers.shutdown();
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
    public long getUpdateCode() {
        return this.updateCode;
    }

    @Override
    public void reload() {
        this.updateCode = System.currentTimeMillis();
    }
}
