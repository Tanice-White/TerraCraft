package io.github.tanice.terraCraft.bukkit.listeners;

import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.events.entity.TerraAttributeUpdateEvent;
import io.github.tanice.terraCraft.bukkit.events.entity.TerraPlayerDataLimitChangeEvent;
import io.github.tanice.terraCraft.bukkit.events.entity.TerraSkillUpdateEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class TerraEventListener implements Listener {

    public TerraEventListener() {
        TerraCraftBukkit.inst().getServer().getPluginManager().registerEvents(this, TerraCraftBukkit.inst());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAttributeUpdate(TerraAttributeUpdateEvent event) {
        TerraCraftBukkit.inst().getEntityAttributeManager().updateAttribute(event.getEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDataLimitChange(TerraPlayerDataLimitChangeEvent event) {
        TerraCraftBukkit.inst().getPlayerDataManager().changePlayerDataLimit(event.getEntity(), event.getDeltaPlayerData());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSkillUpdate(TerraSkillUpdateEvent event) {
        TerraCraftBukkit.inst().getSkillManager().updatePlayerSkills(event.getEntity());
    }

    public void reload() {

    }

    public void unload() {

    }
}
