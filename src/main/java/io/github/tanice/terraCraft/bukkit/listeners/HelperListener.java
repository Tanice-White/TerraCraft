package io.github.tanice.terraCraft.bukkit.listeners;

import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.listeners.helper.MythicListener;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;

public class HelperListener implements Listener {

    private static final String MM = "MythicMobs";
    private MythicListener mythicListener;

    public HelperListener() {
        TerraCraftBukkit.inst().getServer().getPluginManager().registerEvents(this, TerraCraftBukkit.inst());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPluginEnable(PluginEnableEvent event) {
        if (event.getPlugin().getName().equals(MM) && mythicListener == null) {
            mythicListener = new MythicListener();
            TerraCraftLogger.success("MythicMobs Detected, skills available");
        }
    }

    public void reload() {

    }

    public void unload() {

    }
}
