package io.github.tanice.terraCraft.bukkit.events.entity;

import io.github.tanice.terraCraft.api.players.TerraPlayerData;
import io.github.tanice.terraCraft.bukkit.utils.annotation.NonnullByDefault;
import org.bukkit.entity.Player;

/**
 * 玩家数据中 最值 更改事件
 */
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
