package io.github.tanice.terraCraft.bukkit.items;

import io.github.tanice.terraCraft.api.items.TerraTool;
import io.github.tanice.terraCraft.api.items.gems.TerraGemCarrier;
import io.github.tanice.terraCraft.api.items.levels.TerraLeveled;
import io.github.tanice.terraCraft.api.items.qualities.TerraQualitative;
import io.github.tanice.terraCraft.api.skills.TerraSkillCarrier;
import io.github.tanice.terraCraft.bukkit.utils.annotation.NonnullByDefault;
import io.github.tanice.terraCraft.core.items.AbstractItem;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

@NonnullByDefault
public class Tool extends AbstractItem implements TerraTool, TerraQualitative, TerraLeveled, TerraSkillCarrier, TerraGemCarrier {

    // TODO 完善 component

    /**
     * 依据内部名称和对应的config文件创建物品
     *
     * @param name 客制化物品内部名称
     * @param cfg  对应的配置文件部分
     */
    public Tool(String name, ConfigurationSection cfg) {
        super(name, cfg);
    }

    @Override
    public String getLevelTemplateName() {
        return "";
    }

    @Override
    public String getQualityGroupName() {
        return "";
    }

    @Override
    public List<String> getSkillNames() {
        return List.of();
    }

    @Override
    public int getGemStackNumber() {
        return 0;
    }
}
