package io.github.tanice.terraCraft.bukkit.items;

import io.github.tanice.terraCraft.api.attribute.AttributeActiveSection;
import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import io.github.tanice.terraCraft.api.items.TerraGem;
import io.github.tanice.terraCraft.bukkit.utils.annotation.NonnullByDefault;
import io.github.tanice.terraCraft.core.attribute.CalculableMeta;
import io.github.tanice.terraCraft.core.items.AbstractItem;
import org.bukkit.configuration.ConfigurationSection;

import static io.github.tanice.terraCraft.core.constants.ConfigKeys.*;

@NonnullByDefault
public class Gem extends AbstractItem implements TerraGem {

    private final double chance;
    private final boolean lossWhenFailed;
    private final TerraCalculableMeta meta;

    /**
     * 依据内部名称和对应的config文件创建物品
     */
    public Gem(String name, ConfigurationSection cfg, AttributeActiveSection aas) {
        super(name, cfg);
        chance = cfg.getDouble(CHANCE, 1);
        lossWhenFailed = cfg.getBoolean(LOSS_WHEN_FAILED, false);
        this.meta = new CalculableMeta(cfg.getConfigurationSection(ATTRIBUTE_SECTION), aas);
    }

    @Override
    public double getChance() {
        return this.chance;
    }

    @Override
    public boolean lossWhenFailed() {
        return this.lossWhenFailed;
    }

    @Override
    public TerraCalculableMeta getMeta() {
        return this.meta;
    }
}
