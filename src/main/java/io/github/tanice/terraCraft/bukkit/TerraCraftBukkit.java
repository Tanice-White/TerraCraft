package io.github.tanice.terraCraft.bukkit;

import io.github.tanice.terraCraft.api.attribute.TerraEntityAttributeManager;
import io.github.tanice.terraCraft.api.buffs.TerraBuffManager;
import io.github.tanice.terraCraft.api.items.TerraItemManager;
import io.github.tanice.terraCraft.api.players.TerraPlayerDataManager;
import io.github.tanice.terraCraft.api.plugin.TerraPlugin;
import io.github.tanice.terraCraft.api.skills.TerraSkillManager;
import io.github.tanice.terraCraft.api.utils.database.TerraDatabaseManager;
import io.github.tanice.terraCraft.api.utils.js.TerraJSEngineManager;
import io.github.tanice.terraCraft.bukkit.commands.buffs.BuffGroupCommand;
import io.github.tanice.terraCraft.bukkit.commands.items.ItemGroupCommand;
import io.github.tanice.terraCraft.bukkit.commands.plugin.ReloadCommand;
import io.github.tanice.terraCraft.bukkit.commands.TerraCraftCommand;
import io.github.tanice.terraCraft.bukkit.listeners.DamageListener;
import io.github.tanice.terraCraft.bukkit.listeners.ItemListener;
import io.github.tanice.terraCraft.bukkit.listeners.HelperListener;
import io.github.tanice.terraCraft.bukkit.listeners.TerraEventListener;
import io.github.tanice.terraCraft.bukkit.utils.scheduler.TerraSchedulers;
import io.github.tanice.terraCraft.core.attribute.EntityAttributeManager;
import io.github.tanice.terraCraft.core.buffs.BuffManager;
import io.github.tanice.terraCraft.core.config.ConfigManager;
import io.github.tanice.terraCraft.core.items.ItemManager;
import io.github.tanice.terraCraft.core.players.PlayerDataManager;
import io.github.tanice.terraCraft.core.skills.SkillManager;
import io.github.tanice.terraCraft.core.utils.database.DatabaseManager;
import io.github.tanice.terraCraft.core.utils.js.JSEngineManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class TerraCraftBukkit extends JavaPlugin implements TerraPlugin {
    private static TerraCraftBukkit instance;

    private JSEngineManager jsEngineManager;
    private DatabaseManager databaseManager;
    private BuffManager buffManager;
    private ItemManager itemManager;

    private EntityAttributeManager entityAttributeManager;
    private SkillManager skillManager;
    private PlayerDataManager playerDataManager;

    private DamageListener damageListener;
    private ItemListener itemListener;
    private HelperListener helperListener;
    private TerraEventListener terraEventListener;

    private TerraCraftCommand terraCraftCommand;

    /* 更改finalDamage方法 */
//    static {
//        ASMHelper.applyModification();
//    }

    @Override
    public void onEnable() {
        instance = this;
        ConfigManager.load();

        helperListener = new HelperListener();
        itemListener = new ItemListener();
        damageListener = new DamageListener();
        terraEventListener = new TerraEventListener();

        jsEngineManager = new JSEngineManager();
        databaseManager = new DatabaseManager(this);
        buffManager = new BuffManager(this);
        itemManager = new ItemManager(this);
        skillManager = new SkillManager(this);

        entityAttributeManager = new EntityAttributeManager();
        playerDataManager = new PlayerDataManager();

        terraCraftCommand = new TerraCraftCommand(this);
        terraCraftCommand.register(new ItemGroupCommand(this));
        terraCraftCommand.register(new BuffGroupCommand(this));
        terraCraftCommand.register(new ReloadCommand());
        terraCraftCommand.enable();
    }

    @Override
    public void onDisable() {
        if (terraCraftCommand != null) terraCraftCommand.unload();
        if (helperListener != null) helperListener.unload();
        if (itemListener != null) itemListener.unload();
        if (damageListener != null) damageListener.unload();
        if (terraEventListener != null) terraEventListener.unload();

        if (jsEngineManager != null) jsEngineManager.close();
        if (buffManager != null) buffManager.unload();
        if (itemManager != null) itemManager.unload();
        if (skillManager != null) skillManager.unload();
        if (entityAttributeManager != null) entityAttributeManager.unload();
        if (playerDataManager != null) playerDataManager.unload();
        if (databaseManager != null) databaseManager.unload();
        TerraSchedulers.shutdown();
    }

    @Override
    public void reload() {
        TerraSchedulers.clear();
        databaseManager.reload();
        ConfigManager.load();

        helperListener.reload();
        itemListener.reload();
        damageListener.reload();
        terraEventListener.reload();

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
    public TerraItemManager getItemManager() {
        return this.itemManager;
    }

    @Override
    public TerraJSEngineManager getJSEngineManager() {
        return this.jsEngineManager;
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
}
