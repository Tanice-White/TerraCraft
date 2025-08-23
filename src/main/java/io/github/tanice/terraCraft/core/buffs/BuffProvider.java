package io.github.tanice.terraCraft.core.buffs;

import io.github.tanice.terraCraft.api.buffs.BuffActiveCondition;
import io.github.tanice.terraCraft.api.buffs.TerraBaseBuff;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.api.attribute.AttributeActiveSection;
import io.github.tanice.terraCraft.bukkit.utils.StringUtil;
import io.github.tanice.terraCraft.core.buffs.impl.AttributeBuff;
import io.github.tanice.terraCraft.core.buffs.impl.RunnableBuff;
import io.github.tanice.terraCraft.core.buffs.impl.TimerBuff;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import io.github.tanice.terraCraft.core.utils.EnumUtil;
import org.bukkit.configuration.ConfigurationSection;

import java.nio.file.Path;
import java.util.*;

public final class BuffProvider {
    private int valid;
    private int other;
    private final Map<String, Set<String>> mutexMap;/* 全局buff冲突 */

    public BuffProvider() {
        valid = 0;
        other = 0;
        mutexMap = new HashMap<>();
    }

    public Optional<TerraBaseBuff> createBuff(String name, ConfigurationSection cfg) {
        if (cfg == null) return Optional.empty();

        AttributeActiveSection aas = EnumUtil.safeValueOf(AttributeActiveSection.class, cfg.getString("section"), AttributeActiveSection.ERROR);
        BuffActiveCondition bac = EnumUtil.safeValueOf(BuffActiveCondition.class, cfg.getString("condition"), BuffActiveCondition.ALL);
        if (checkAttributeActiveSection(name, aas)) {
            for (String m : StringUtil.splitByComma(cfg.getString("mutex"))) this.addMutex(name, m);
            return Optional.of(new AttributeBuff(name, cfg, mutexMap.computeIfAbsent(name, k -> new HashSet<>()), aas, bac));
        }
        else return Optional.empty();
    }

    public Optional<TerraBaseBuff> createBuff(String jsFileName, Path path) {
        Map<String, String> vars = TerraCraftBukkit.inst().getJSEngineManager().loadScript(jsFileName, path);

        String name = vars.getOrDefault("terra_name", jsFileName);
        String displayName = vars.getOrDefault("display_name", name);
        boolean enable = Boolean.parseBoolean(vars.getOrDefault("enable", "true"));
        int priority = (int) Double.parseDouble(vars.getOrDefault("priority", "2147483647"));
        double chance = Double.parseDouble(vars.getOrDefault("chance", "1"));
        int duration = (int) Double.parseDouble(vars.getOrDefault("duration", "20"));
        AttributeActiveSection aas = EnumUtil.safeValueOf(AttributeActiveSection.class, vars.get("section"), AttributeActiveSection.ERROR);
        BuffActiveCondition bac = EnumUtil.safeValueOf(BuffActiveCondition.class, vars.get("condition"), BuffActiveCondition.ALL);
        Collection<String> mutex = StringUtil.splitByComma(vars.getOrDefault("mutex", null));
        for (String m : mutex) this.addMutex(name, m);
        Collection<String> override = StringUtil.splitByComma(vars.getOrDefault("override", null));
        int cd = (int) Double.parseDouble(vars.getOrDefault("cd", "20"));

        if (checkAttributeActiveSection(name, aas)) {
            if (aas == AttributeActiveSection.TIMER) return Optional.of(new TimerBuff(jsFileName, name, displayName, enable, priority, chance, duration, mutexMap.computeIfAbsent(name, k -> new HashSet<>()), override, bac, aas, cd));
            else return Optional.of(new RunnableBuff(jsFileName, name, displayName, enable, priority, chance, duration, mutexMap.computeIfAbsent(name, k -> new HashSet<>()), override, bac, aas));
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

    public Map<String, Set<String>> getMutexMap() {
        return this.mutexMap;
    }

    public void reload() {
        valid = 0;
        other = 0;
        mutexMap.clear();
    }

    private boolean checkAttributeActiveSection(String name, AttributeActiveSection aas) {
        if (aas == AttributeActiveSection.ERROR) {
            TerraCraftLogger.warning("buff: " + name + " AttributeActiveSection read failure (ERROR as default). This section will be unavailable for calculation");
            other ++;
            return false;
        }
        valid ++;
        return true;
    }

    private void addMutex(String a, String b) {
        mutexMap.computeIfAbsent(a, k -> new HashSet<>()).add(b);
        mutexMap.computeIfAbsent(b, k -> new HashSet<>()).add(a);
    }
}
