package io.github.tanice.terraCraft.bukkit.event.entity;

import io.github.tanice.terraCraft.bukkit.event.AbstractTerraEvent;
import io.github.tanice.terraCraft.bukkit.util.annotation.NonnullByDefault;
import org.bukkit.entity.Player;

// TODO 需要公示哪些信息
@NonnullByDefault
public class TerraPlayerDataLimitChangeEvent extends AbstractTerraEvent {


    public TerraPlayerDataLimitChangeEvent(Player player) {
        super(player);
    }

    public Player getEntity() {
        return (Player) this.entity;
    }
}
