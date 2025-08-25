package io.github.tanice.terraCraft.bukkit.event.entity;

import io.github.tanice.terraCraft.api.player.TerraPlayerData;
import io.github.tanice.terraCraft.bukkit.event.AbstractTerraEvent;
import io.github.tanice.terraCraft.bukkit.util.annotation.NonnullByDefault;
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
