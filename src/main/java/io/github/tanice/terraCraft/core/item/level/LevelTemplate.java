package io.github.tanice.terraCraft.core.item.level;

import io.github.tanice.terraCraft.api.attribute.AttributeActiveSection;
import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import io.github.tanice.terraCraft.api.item.level.TerraLevelTemplate;
import io.github.tanice.terraCraft.core.attribute.CalculableMeta;
import org.bukkit.configuration.ConfigurationSection;

import static io.github.tanice.terraCraft.core.constant.ConfigKeys.*;
import static io.github.tanice.terraCraft.core.util.EnumUtil.safeValueOf;

public class LevelTemplate implements TerraLevelTemplate {
    private final String name;
    private final int begin;
    private final int max;
    private final double chance;
    /** 升级所需物品的内部名 */
    private final String material;
    private final boolean failedLevelDown;
    private final TerraCalculableMeta meta;

    public LevelTemplate(String name, ConfigurationSection cfg) {
        this.name = name;
        begin = cfg.getInt(BEGIN, 0);
        max = cfg.getInt(MAX, 100);
        chance = cfg.getDouble(CHANCE, 1);
        /* items inner name */
        material = cfg.getString(LEVEL_UP_NEED, "");
        failedLevelDown = cfg.getBoolean(LEVEL_DOWN_WHEN_FAILED, false);
        meta = new CalculableMeta(cfg.getConfigurationSection("attribute"), safeValueOf(AttributeActiveSection.class, cfg.getString("section"), AttributeActiveSection.ERROR));
    }

    public String getName() {
        return this.name;
    }

    public int getBegin() {
        return this.begin;
    }

    public int getMax() {
        return this.max;
    }

    public double getChance() {
        return this.chance;
    }

    public String getMaterial() {
        return this.material;
    }

    public boolean isFailedLevelDown() {
        return this.failedLevelDown;
    }

    public TerraCalculableMeta getMeta() {
        return this.meta.clone();
    }
}
