package io.github.tanice.terraCraft.bukkit;

import io.github.tanice.terraCraft.api.attribute.TerraEntityAttributeManager;
import io.github.tanice.terraCraft.api.buff.TerraBuffManager;
import io.github.tanice.terraCraft.api.item.TerraItemManager;
import io.github.tanice.terraCraft.api.player.TerraPlayerDataManager;
import io.github.tanice.terraCraft.api.plugin.TerraPlugin;
import io.github.tanice.terraCraft.api.skill.TerraSkillManager;
import io.github.tanice.terraCraft.api.database.TerraDatabaseManager;
import io.github.tanice.terraCraft.api.js.TerraJSEngineManager;
import io.github.tanice.terraCraft.bukkit.command.attribute.AttributeGroupCommand;
import io.github.tanice.terraCraft.bukkit.command.buff.BuffGroupCommand;
import io.github.tanice.terraCraft.bukkit.command.gem.GemGroupCommand;
import io.github.tanice.terraCraft.bukkit.command.item.ItemGroupCommand;
import io.github.tanice.terraCraft.bukkit.command.plugin.ReloadCommand;
import io.github.tanice.terraCraft.bukkit.command.TerraCraftCommand;
import io.github.tanice.terraCraft.bukkit.listener.DamageListener;
import io.github.tanice.terraCraft.bukkit.listener.HelperListener;
import io.github.tanice.terraCraft.bukkit.listener.TerraEventListener;
import io.github.tanice.terraCraft.bukkit.util.scheduler.TerraSchedulers;
import io.github.tanice.terraCraft.core.attribute.EntityAttributeManager;
import io.github.tanice.terraCraft.core.buff.BuffManager;
import io.github.tanice.terraCraft.core.config.ConfigManager;
import io.github.tanice.terraCraft.core.item.ItemManager;
import io.github.tanice.terraCraft.core.player.PlayerDataManager;
import io.github.tanice.terraCraft.core.skill.SkillManager;
import io.github.tanice.terraCraft.core.util.database.DatabaseManager;
import io.github.tanice.terraCraft.core.util.helper.asm.ASMHelper;
import io.github.tanice.terraCraft.core.util.js.JSEngineManager;
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
    private HelperListener helperListener;
    private TerraEventListener terraEventListener;

    private TerraCraftCommand terraCraftCommand;

    /* 更改finalDamage方法 */
    static {
        ASMHelper.applyModification();
    }

    @Override
    public void onEnable() {
        instance = this;
        ConfigManager.load();

        helperListener = new HelperListener();
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
        terraCraftCommand.register(new AttributeGroupCommand(this));
        terraCraftCommand.register(new GemGroupCommand(this));
        terraCraftCommand.register(new ReloadCommand());
        terraCraftCommand.enable();
    }

    @Override
    public void onDisable() {
        if (terraCraftCommand != null) terraCraftCommand.unload();
        if (helperListener != null) helperListener.unload();
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
