package io.github.tanice.terraCraft.core.buffs;

import io.github.tanice.terraCraft.api.buffs.BuffActiveCondition;
import io.github.tanice.terraCraft.api.buffs.TerraBaseBuff;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.api.attribute.AttributeActiveSection;
import io.github.tanice.terraCraft.core.buffs.impl.AttributeBuff;
import io.github.tanice.terraCraft.core.buffs.impl.RunnableBuff;
import io.github.tanice.terraCraft.core.buffs.impl.TimerBuff;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import io.github.tanice.terraCraft.core.utils.EnumUtil;
import org.bukkit.configuration.ConfigurationSection;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import static io.github.tanice.terraCraft.core.constants.ConfigKeys.*;

public final class BuffProvider {

    private int valid;
    private int other;

    public BuffProvider() {
    }

    public Optional<TerraBaseBuff> createBuff(String name, ConfigurationSection cfg) {
        if (cfg == null) return Optional.empty();

        AttributeActiveSection aas = EnumUtil.safeValueOf(AttributeActiveSection.class, cfg.getString(ACTIVE_SECTION), AttributeActiveSection.ERROR);
        BuffActiveCondition bac = EnumUtil.safeValueOf(BuffActiveCondition.class, cfg.getString(BUFF_ACTIVE_CONDITION), BuffActiveCondition.ALL);
        if (checkAttributeActiveSection(name, aas)) return Optional.of(new AttributeBuff(name, cfg, aas, bac));
        else return Optional.empty();
    }

    public Optional<TerraBaseBuff> createBuff(String jsFileName, Path path) {
        Map<String, String> vars = TerraCraftBukkit.inst().getJSEngineManager().loadScript(jsFileName, path);

        String name = vars.getOrDefault(NAME, jsFileName);
        String displayName = vars.getOrDefault(DISPLAY_NAME, name);
        boolean enable = Boolean.parseBoolean(vars.getOrDefault(ENABLE, "true"));
        int priority = (int) Double.parseDouble(vars.getOrDefault(PRIORITY, "2147483647"));
        double chance = Double.parseDouble(vars.getOrDefault(CHANCE, "1"));
        int duration = (int) Double.parseDouble(vars.getOrDefault(DURATION, "20"));
        AttributeActiveSection aas = EnumUtil.safeValueOf(AttributeActiveSection.class, vars.get(ACTIVE_SECTION), AttributeActiveSection.ERROR);
        BuffActiveCondition bac = EnumUtil.safeValueOf(BuffActiveCondition.class, vars.get(BUFF_ACTIVE_CONDITION), BuffActiveCondition.ALL);
        int cd = (int) Double.parseDouble(vars.getOrDefault(CD, "20"));

        if (checkAttributeActiveSection(name, aas)) {
            if (aas == AttributeActiveSection.TIMER) return Optional.of(new TimerBuff(jsFileName, name, displayName, enable, priority, chance, duration, bac, aas, cd));
            else return Optional.of(new RunnableBuff(jsFileName, name, displayName, enable, priority, chance, duration, bac, aas));
        } else return Optional.empty();
    }

    public int getTotal() {
        return this.valid + this.other;
    }

    public int getValid() {
        return this.valid;
    }

    public int getOther() {
        return this.other;
    }

    private boolean checkAttributeActiveSection(String name, AttributeActiveSection aas) {
        if (aas == AttributeActiveSection.ERROR) {
            TerraCraftLogger.warning("buff: " + name + " AttributeActiveSection read failure (OTHER as default). This section will be unavailable for calculation");
            other ++;
            return false;
        }
        valid ++;
        return true;
    }
}
