package io.github.tanice.terraCraft.bukkit.events.entity;

import io.github.tanice.terraCraft.bukkit.events.AbstractTerraEvent;
import io.github.tanice.terraCraft.bukkit.utils.annotation.NonnullByDefault;
import org.bukkit.entity.Player;

@NonnullByDefault
public class TerraPlayerEatEvent extends AbstractTerraEvent {

    private final TerraEdible terraEdible;

    public TerraPlayerEatEvent(Player player, TerraEdible terraEdible) {
        super(player);
        this.terraEdible = terraEdible;
    }

    public Player getEntity() {
        return (Player) this.entity;
    }

    public TerraEdible getEdible() {
        return this.terraEdible;
    }
}
