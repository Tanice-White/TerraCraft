package io.github.tanice.terraCraft.bukkit.events.entity;

import io.github.tanice.terraCraft.api.players.TerraPlayerData;
import io.github.tanice.terraCraft.bukkit.events.AbstractTerraEvent;
import io.github.tanice.terraCraft.bukkit.utils.annotation.NonnullByDefault;
import org.bukkit.entity.Player;

@NonnullByDefault
public class TerraPlayerDataLimitChangeEvent extends AbstractTerraEvent {

    private final TerraPlayerData deltaPlayerData;

    public TerraPlayerDataLimitChangeEvent(Player player, TerraPlayerData deltaPlayerData) {
        super(player);
        this.deltaPlayerData = deltaPlayerData;
    }

    public Player getEntity() {
        return (Player) this.entity;
    }

    public TerraPlayerData getDeltaPlayerData() {
        return this.deltaPlayerData;
    }
}
