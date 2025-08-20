package io.github.tanice.terraCraft.bukkit.items;

import io.github.tanice.terraCraft.api.items.TerraItem;
import io.github.tanice.terraCraft.api.items.components.*;
import io.github.tanice.terraCraft.bukkit.items.components.*;
import io.github.tanice.terraCraft.core.items.AbstractItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.*;

public class Item extends AbstractItem implements TerraItem {
    private final String name;
    @Nullable
    private BuffComponent buffComponent;
    @Nullable
    private CommandsComponent commandsComponent;
    @Nullable
    private DamageTypeComponent damageTypeComponent;
    @Nullable
    private DurabilityComponent durabilityComponent;
    @Nullable
    private GemComponent gemComponent;
    @Nullable
    private GemHolderComponent gemHolderComponent;

    private final TerraNameComponent terraNameComponent;
    @Nullable
    private LevelComponent levelComponent;
    @Nullable
    private MetaComponent metaComponent;
    @Nullable
    private QualityComponent qualityComponent;
    @Nullable
    private SkillComponent skillComponent;

    private final UpdateCodeComponent updateCodeComponent;
    /**
     * 依据内部名称和对应的config文件创建mc基础物品
     */
    public Item(String id, ConfigurationSection cfg) {
        super(cfg);
        this.name = id;

        ConfigurationSection sub;
        if (cfg.isSet("buff")) {
            sub = cfg.getConfigurationSection("buff");
            if (sub != null) buffComponent = new BuffComponent(sub);
        }
        if (cfg.isSet("command")) {
            sub = cfg.getConfigurationSection("command");
            if (sub != null) commandsComponent = new CommandsComponent(sub);
        }
        if (cfg.isSet("damage_type")) {
            sub = cfg.getConfigurationSection("damage_type");
            if (sub != null) damageTypeComponent = new DamageTypeComponent(sub);
        }
        if (cfg.isSet("terra_durability")) {
            sub = cfg.getConfigurationSection("durability");
            if (sub != null) durabilityComponent = new DurabilityComponent(sub);
        }
        if (cfg.isSet("gem")) {
            sub = cfg.getConfigurationSection("gem");
            if (sub != null) gemComponent = new GemComponent(sub);
        }
        if (cfg.isSet("gem_holder")) {
            sub = cfg.getConfigurationSection("gem_holder");
            if (sub != null) gemHolderComponent = new GemHolderComponent(sub);
        }
        if (cfg.isSet("level")) {
            sub = cfg.getConfigurationSection("level");
            if (sub != null) levelComponent = new LevelComponent(sub);
        }
        if (cfg.isSet("meta")) {
            sub = cfg.getConfigurationSection("meta");
            if (sub != null) metaComponent = new MetaComponent(sub);
        }
        if (cfg.isSet("quality")) {
            sub = cfg.getConfigurationSection("quality");
            if (sub != null) qualityComponent = new QualityComponent(sub);
        }
        if (cfg.isSet("skill")) {
            sub = cfg.getConfigurationSection("skill");
            if (sub != null) skillComponent = new SkillComponent(sub);
        }

        this.terraNameComponent = new TerraNameComponent(id);
        this.updateCodeComponent = new UpdateCodeComponent(this.hashCode());
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void selfUpdate(ItemStack old) {
        if (UpdateCodeComponent.from(old).getCode() == this.updateCodeComponent.getCode()) return;
        super.selfUpdate(old);
        /* innerName 不能被更改 */
        /* updateCode 必须被更改 */
        updateCodeComponent.apply(old);
        if (buffComponent != null && buffComponent.canUpdate()) buffComponent.updatePartial().apply(old);
        if (commandsComponent != null && commandsComponent.canUpdate()) commandsComponent.updatePartial().apply(old);
        if (damageTypeComponent != null && damageTypeComponent.canUpdate()) damageTypeComponent.updatePartial().apply(old);
        if (durabilityComponent != null && durabilityComponent.canUpdate()) durabilityComponent.updatePartial().apply(old);
        if (gemComponent != null && gemComponent.canUpdate()) gemComponent.updatePartial().apply(old);
        if (gemHolderComponent != null && gemHolderComponent.canUpdate()) gemHolderComponent.updatePartial().apply(old);
        if (levelComponent != null && levelComponent.canUpdate()) levelComponent.updatePartial().apply(old);
        if (metaComponent != null && metaComponent.canUpdate()) metaComponent.updatePartial().apply(old);
        if (qualityComponent != null && qualityComponent.canUpdate()) qualityComponent.updatePartial().apply(old);
        if (skillComponent != null && skillComponent.canUpdate()) skillComponent.updatePartial().apply(old);
    }

    @Override
    @Nullable
    public TerraBuffComponent getBuffComponent() {
        return buffComponent;
    }

    @Override
    @Nullable
    public TerraCommandsComponent getCommandComponent() {
        return commandsComponent;
    }

    @Override
    @Nullable
    public TerraDamageTypeComponent getDamageTypeComponent() {
        return damageTypeComponent;
    }

    @Override
    @Nullable
    public TerraDurabilityComponent getDurabilityComponent() {
        return durabilityComponent;
    }

    @Override
    @Nullable
    public TerraGemComponent getGemComponent() {
        return gemComponent;
    }

    @Override
    @Nullable
    public TerraGemHolderComponent getGemHolderComponent() {
        return gemHolderComponent;
    }

    @Override
    public TerraInnerNameComponent getInnerNameComponent() {
        return terraNameComponent;
    }

    @Override
    @Nullable
    public TerraLevelComponent getLevelComponent() {
        return levelComponent;
    }

    @Override
    @Nullable
    public TerraMetaComponent getMetaComponent() {
        return metaComponent;
    }

    @Override
    @Nullable
    public TerraQualityComponent getQualityComponent() {
        return qualityComponent;
    }

    @Override
    @Nullable
    public TerraSkillComponent getSkillComponent() {
        return skillComponent;
    }

    @Override
    public TerraUpdateCodeComponent getUpdateCodeComponent() {
        return updateCodeComponent;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                super.hashCode(), name, terraNameComponent,
                buffComponent, commandsComponent, damageTypeComponent, durabilityComponent, gemComponent,
                gemHolderComponent, levelComponent, metaComponent, qualityComponent, skillComponent
        );
    }
}
