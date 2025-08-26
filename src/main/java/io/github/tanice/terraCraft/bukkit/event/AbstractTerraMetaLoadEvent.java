package io.github.tanice.terraCraft.bukkit.event;

import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import io.github.tanice.terraCraft.bukkit.util.annotation.NonnullByDefault;
import io.github.tanice.terraCraft.core.attribute.CalculableMeta;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nullable;

@NonnullByDefault
public abstract class AbstractTerraMetaLoadEvent extends Event {
    protected static final HandlerList handlers = new HandlerList();

    @Nullable
    protected TerraCalculableMeta meta;

    public AbstractTerraMetaLoadEvent() {

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
}
