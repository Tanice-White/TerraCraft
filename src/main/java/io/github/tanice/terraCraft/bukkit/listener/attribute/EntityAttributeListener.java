package io.github.tanice.terraCraft.bukkit.listener.attribute;

import io.github.tanice.terraCraft.api.listener.TerraListener;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.papermc.paper.event.entity.EntityEquipmentChangedEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

    }

    @EventHandler
    public void onEntityEquipmentChange(EntityEquipmentChangedEvent event) {
        LivingEntity entity = event.getEntity();
        /* activateHoldBuffs() 会自动触发属性变动 */
        TerraCraftBukkit.inst().getBuffManager().activateHoldBuffs(entity);
        if (event instanceof Player p) TerraCraftBukkit.inst().getSkillManager().updatePlayerSkills(p);
    }
}
