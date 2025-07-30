package io.github.tanice.terraCraft.bukkit.events.entity;

import io.github.tanice.terraCraft.bukkit.events.AbstractTerraEvent;
import io.github.tanice.terraCraft.bukkit.utils.annotation.NonnullByDefault;
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
