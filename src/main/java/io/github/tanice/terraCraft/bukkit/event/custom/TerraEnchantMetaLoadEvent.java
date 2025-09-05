package io.github.tanice.terraCraft.bukkit.event.custom;

import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import io.github.tanice.terraCraft.bukkit.util.annotation.NonnullByDefault;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nullable;

@NonnullByDefault
public class TerraEnchantMetaLoadEvent extends Event {
    protected static final HandlerList handlers = new HandlerList();

    @Nullable
    protected TerraCalculableMeta meta;
    private final String bukkitEnchantId;

    public TerraEnchantMetaLoadEvent(String enchantId) {
        this.meta = null;
        this.bukkitEnchantId = enchantId;
    }
    public static HandlerList getHandlerList() {return handlers;}

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Nullable
    public TerraCalculableMeta getMeta() {
        return this.meta;
    }

    public void setMeta(TerraCalculableMeta meta) {
        this.meta = meta;
    }

    public String getEnchantId() {
        return this.bukkitEnchantId;
    }
}