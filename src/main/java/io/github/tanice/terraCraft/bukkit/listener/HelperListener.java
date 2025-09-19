package io.github.tanice.terraCraft.bukkit.listener;

import io.github.tanice.terraCraft.api.listener.TerraListener;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.listener.helper.MythicListener;
import io.github.tanice.terraCraft.bukkit.util.papi.TerraPlaceholder;
import io.github.tanice.terraCraft.core.util.logger.TerraCraftLogger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;

public class HelperListener implements Listener, TerraListener {

    private static final String MM = "MythicMobs";
    private static final String PAPI = "PlaceholderAPI";
    private MythicListener mythicListener;

    public HelperListener() {
        TerraCraftBukkit.inst().getServer().getPluginManager().registerEvents(this, TerraCraftBukkit.inst());
    }

    @Override
    public void reload() {
        if (mythicListener != null) mythicListener.reload();
    }

    @Override
    public void unload() {
        if (mythicListener != null) mythicListener.unload();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPluginEnable(PluginEnableEvent event) {
        if (event.getPlugin().getName().equals(MM) && mythicListener == null) {
            mythicListener = new MythicListener();
            TerraCraftLogger.success("MythicMobs detected, skills available");
            return;
        }
        if (event.getPlugin().getName().equals(PAPI)) {
            new TerraPlaceholder().register();
            TerraCraftLogger.success("PlaceholderAPI detected, papi available");
            return;
        }
    }
}
