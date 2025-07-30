package io.github.tanice.terraCraft.bukkit.events.load;

import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import io.github.tanice.terraCraft.bukkit.utils.annotation.NonnullByDefault;
import io.github.tanice.terraCraft.core.attribute.CalculableMeta;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@NonnullByDefault
public class AbstractTerraMetaLoadEvent extends Event {
    protected static final HandlerList handlers = new HandlerList();

    protected final TerraCalculableMeta meta;

    public AbstractTerraMetaLoadEvent() {
        this.meta = new CalculableMeta();
    }

    public static HandlerList getHandlerList() {return handlers;}

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public TerraCalculableMeta getMeta() {
        return this.meta;
    }
}
