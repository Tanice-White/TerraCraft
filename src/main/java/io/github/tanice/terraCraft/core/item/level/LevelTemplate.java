package io.github.tanice.terraCraft.core.item.level;

import io.github.tanice.terraCraft.api.attribute.AttributeActiveSection;
import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import io.github.tanice.terraCraft.api.item.level.TerraLevelTemplate;
import io.github.tanice.terraCraft.core.attribute.CalculableMeta;
import io.github.tanice.terraCraft.core.config.ConfigManager;
import io.github.tanice.terraCraft.core.util.expression.TerraExpression;
import io.github.tanice.terraCraft.core.util.logger.TerraCraftLogger;
import org.bukkit.configuration.ConfigurationSection;
import org.codehaus.commons.compiler.CompileException;

import static io.github.tanice.terraCraft.api.command.TerraCommand.*;
import static io.github.tanice.terraCraft.core.util.EnumUtil.safeValueOf;

public class LevelTemplate implements TerraLevelTemplate {
    private final String name;
    private final int begin;
    private final int max;
    private final String chanceExpr;
    /** 升级所需物品的内部名 */
    private final String material;
    private final boolean failedLevelDown;
    private final TerraCalculableMeta meta;

    public LevelTemplate(String name, ConfigurationSection cfg) {
        this.name = name;
        begin = cfg.getInt("begin", 0);
        max = cfg.getInt("max", 100);
        chanceExpr = cfg.getString("chance", "1");
        /* items inner name */
        material = cfg.getString("level_up_need", "");
        failedLevelDown = cfg.getBoolean("fail_level_down", false);
        meta = new CalculableMeta(cfg.getConfigurationSection("attribute"), safeValueOf(AttributeActiveSection.class, cfg.getString("section"), AttributeActiveSection.ERROR));
        try {
            TerraExpression.register(
                    chanceExpr,
                    chanceExpr,
                    double.class,
                    new String[]{"begin", "max", "level"},
                    /* int的计算会直接导致整个计算过程都是int，得到的结果是0 */
                    new Class[]{double.class, double.class, double.class}
            );
        } catch (CompileException e) {
            TerraCraftLogger.error("Failed to register chance expression: " + chanceExpr + " in " + cfg.getCurrentPath());
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getBegin() {
        return this.begin;
    }

    @Override
    public int getMax() {
        return this.max;
    }

    @Override
    public double getChance(int currentLevel) {
        try {
            double chance = (double) TerraExpression.calculate(chanceExpr, new Object[]{begin, max, currentLevel});
            if (ConfigManager.isDebug())
                TerraCraftLogger.debug(TerraCraftLogger.DebugLevel.EXPRESSION, "expression: " + chanceExpr + ", chance result=" + chance);
            return chance;
        } catch (Exception e) {
            TerraCraftLogger.error("Error when calculating chance in template: " + name + ". \n" + e.getMessage());
            return 0;
        }
    }

    @Override
    public String getMaterial() {
        return this.material;
    }

    @Override
    public boolean isFailedLevelDown() {
        return this.failedLevelDown;
    }

    @Override
    public TerraCalculableMeta getMeta() {
        return this.meta.clone();
    }

    @Override
    public String toString() {
        return BOLD + YELLOW + "LevelTemplate:" + "\n" +
                "    " + AQUA + "name:" + WHITE + name + "\n" +
                "    " + AQUA + "begin:" + WHITE + begin + "\n" +
                "    " + AQUA + "max:" + WHITE + max + "\n" +
                "    " + AQUA + "chance:" + WHITE + chanceExpr + "\n" +
                "    " + AQUA + "material:" + WHITE + (material.isEmpty() ? "none" : material) + "\n" +
                "    " + AQUA + "failed_level_down:" + WHITE + failedLevelDown + "\n" +
                meta + RESET;
    }
}
