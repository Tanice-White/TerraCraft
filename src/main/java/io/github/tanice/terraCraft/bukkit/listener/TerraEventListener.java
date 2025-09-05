package io.github.tanice.terraCraft.bukkit.listener;

import io.github.tanice.terraCraft.api.listener.TerraListener;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.event.entity.TerraAttributeUpdateEvent;
import io.github.tanice.terraCraft.bukkit.event.entity.TerraPlayerDataLimitChangeEvent;
import io.github.tanice.terraCraft.bukkit.event.entity.TerraSkillUpdateEvent;
import io.github.tanice.terraCraft.bukkit.event.item.TerraItemUpdateEvent;
import io.github.tanice.terraCraft.bukkit.listener.attribute.EntityAttributeListener;
import io.github.tanice.terraCraft.bukkit.listener.item.ItemOperationListener;
import io.github.tanice.terraCraft.bukkit.listener.item.ItemUpdateListener;
import io.github.tanice.terraCraft.core.config.ConfigManager;
import io.github.tanice.terraCraft.core.util.logger.TerraCraftLogger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class TerraEventListener implements Listener, TerraListener {

    private final EntityAttributeListener entityAttributeListener;
    private final ItemOperationListener itemOperationListener;
    private final ItemUpdateListener itemUpdateListener;

    public TerraEventListener() {
        entityAttributeListener = new EntityAttributeListener();
        itemOperationListener = new ItemOperationListener();
        itemUpdateListener = new ItemUpdateListener();
        TerraCraftBukkit.inst().getServer().getPluginManager().registerEvents(this, TerraCraftBukkit.inst());
    }

    @Override
    public void reload() {
        if (entityAttributeListener != null) entityAttributeListener.reload();
        if (itemOperationListener != null) itemUpdateListener.reload();
        if (itemUpdateListener != null) itemUpdateListener.reload();
    }

    @Override
    public void unload() {
        if (entityAttributeListener != null) entityAttributeListener.unload();
        if (itemOperationListener != null) itemUpdateListener.unload();
        if (itemUpdateListener != null) itemUpdateListener.unload();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAttributeUpdate(TerraAttributeUpdateEvent event) {
        TerraCraftBukkit.inst().getEntityAttributeManager().updateAttribute(event.getEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDataLimitChange(TerraPlayerDataLimitChangeEvent event) {
        // TODO 玩家NBT数据写入
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSkillUpdate(TerraSkillUpdateEvent event) {
        TerraCraftBukkit.inst().getSkillManager().updatePlayerSkills(event.getEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemUpdate(TerraItemUpdateEvent event) {
        Player player = event.getPlayer();
        // TODO 操作buff attribute skills
        // TODO 更新lore
        TerraCraftBukkit.inst().getBuffManager().activateHoldBuffs(player);
        if (ConfigManager.isDebug())
            TerraCraftLogger.debug(TerraCraftLogger.DebugLevel.ITEM, "Item terra name: " + event.getItemTerraName() + " in player " + player.getName() + " updated");
    }
}
