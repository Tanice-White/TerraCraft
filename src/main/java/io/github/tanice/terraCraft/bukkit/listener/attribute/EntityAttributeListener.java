package io.github.tanice.terraCraft.bukkit.listener.attribute;

import io.github.tanice.terraCraft.api.listener.TerraListener;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.util.scheduler.TerraSchedulers;
import io.github.tanice.terraCraft.core.util.registry.Registry;
import io.papermc.paper.event.entity.EntityEquipmentChangedEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * 监听所有会导致实体属性(buff attribute这里是meta用于伤害计算 skill)变化的事件
 */
public class EntityAttributeListener implements Listener, TerraListener {

    public EntityAttributeListener() {
        TerraCraftBukkit.inst().getServer().getPluginManager().registerEvents(this, TerraCraftBukkit.inst());
    }

    @Override
    public void reload() {

    }

    @Override
    public void unload() {

    }

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event) {
        TerraSchedulers.sync().runLater(() -> {
            // TODO operate database
            updateAttribute(event.getPlayer());
        }, 1);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // TODO operate database
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        TerraCraftBukkit plugin = TerraCraftBukkit.inst();
        plugin.getBuffManager().deactivateEntityBuffs(player);
        plugin.getEntityAttributeManager().unregister(player);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        TerraSchedulers.sync().runLater(() -> updateAttribute(event.getPlayer()), 1);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityEquipmentChange(EntityEquipmentChangedEvent event) {
        TerraSchedulers.sync().runLater(() -> updateAttribute(event.getEntity()), 1);
    }

    /** 药水效果监听 */
    @EventHandler(ignoreCancelled = true)
    public void onPotionEffect(EntityPotionEffectEvent event) {
        /* 过滤不需要监听的药药水效果 */
        if (Registry.ORI_POTION.get(event.getModifiedType().getKey().getKey().toLowerCase()) == null) return;
        TerraSchedulers.sync().runLater(() -> {
            TerraCraftBukkit.inst().getEntityAttributeManager().updateAttribute((LivingEntity) event.getEntity());
        }, 1);
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        TerraSchedulers.sync().runLater(() -> updateAttribute(entity), 1);
    }

    private void updateAttribute(LivingEntity entity) {
        /* activateHoldBuffs() 会自动触发属性变动 */
        TerraCraftBukkit.inst().getEntityAttributeManager().updateAttribute(entity);
        TerraCraftBukkit.inst().getBuffManager().activateHoldBuffs(entity);
        /* buff没有更新则需要手动触发更新 */
        if (entity instanceof Player p) TerraCraftBukkit.inst().getSkillManager().updatePlayerSkills(p);
    }
}
