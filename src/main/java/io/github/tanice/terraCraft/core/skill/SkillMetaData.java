package io.github.tanice.terraCraft.core.skill;

import org.bukkit.configuration.ConfigurationSection;


public class SkillMetaData {
    /** 技能名 */
    private final String skillName;
    private final String mythicSkillName;
    /** 技能基础 cd (tick) */
    private final int cd;
    /** 技能基础的蓝耗 */
    private final double manaCost;

    public SkillMetaData(String skillName, ConfigurationSection cfg) {
        this.skillName = skillName;
        String mn = cfg.getString("mythic_name");
        this.mythicSkillName = mn == null ? skillName : mn;
        this.cd = cfg.getInt("cd", 0);
        this.manaCost = cfg.getDouble("mana_cost", 0D);
    }

    public String getSkillName() {
        return this.skillName;
    }

    public String getMythicSkillName() {
        return this.mythicSkillName;
    }

    public int getCd() {
        return this.cd;
    }

    public double getManaCost() {
        return this.manaCost;
    }
}
