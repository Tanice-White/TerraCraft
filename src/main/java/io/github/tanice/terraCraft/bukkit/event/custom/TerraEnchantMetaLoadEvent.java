package io.github.tanice.terraCraft.bukkit.event.custom;

import io.github.tanice.terraCraft.bukkit.event.AbstractTerraMetaLoadEvent;
import io.github.tanice.terraCraft.bukkit.util.annotation.NonnullByDefault;

@NonnullByDefault
public class TerraEnchantMetaLoadEvent extends AbstractTerraMetaLoadEvent {

    private final String enchantName;

    public TerraEnchantMetaLoadEvent(String enchantName) {
        super();
        this.enchantName = enchantName;
    }

    public String getEnchantName() {
        return this.enchantName;
    }
}
