package io.github.tanice.terraCraft.bukkit.event.entity;

import io.github.tanice.terraCraft.bukkit.event.AbstractTerraEvent;
import io.github.tanice.terraCraft.bukkit.util.annotation.NonnullByDefault;
import org.bukkit.entity.Player;

@NonnullByDefault
public class TerraSkillUpdateEvent extends AbstractTerraEvent {

    public TerraSkillUpdateEvent(Player player) {
        super(player);
    }

    public Player getEntity() {
        return (Player) this.entity;
    }
}
