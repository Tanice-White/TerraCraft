package io.github.tanice.terraCraft.bukkit.events.calculate;

import io.github.tanice.terraCraft.bukkit.utils.annotation.NonnullByDefault;

@NonnullByDefault
public class TerraEnchantMetaLoadEvent extends AbstractTerraMetaLoadEvent{

    private final String enchantName;

    public TerraEnchantMetaLoadEvent(String enchantName) {
        super();
        this.enchantName = enchantName;
    }

    public String getEnchantName() {
        return this.enchantName;
    }
}
