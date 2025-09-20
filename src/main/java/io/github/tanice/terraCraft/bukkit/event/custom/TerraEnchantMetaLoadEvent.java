package io.github.tanice.terraCraft.bukkit.event.custom;

import io.github.tanice.terraCraft.bukkit.util.annotation.NonnullByDefault;
import org.bukkit.event.HandlerList;

@NonnullByDefault
public class TerraEnchantMetaLoadEvent extends AbstractTerraMetaLoadEvent {
    private final String bukkitEnchantId;

    public TerraEnchantMetaLoadEvent(String enchantId) {
        super();
        this.bukkitEnchantId = enchantId;
    }

    public String getEnchantId() {
        return this.bukkitEnchantId;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}