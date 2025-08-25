package io.github.tanice.terraCraft.bukkit.item;

import io.github.tanice.terraCraft.api.item.TerraItem;
import io.github.tanice.terraCraft.api.item.component.*;
import io.github.tanice.terraCraft.bukkit.item.component.*;
import io.github.tanice.terraCraft.core.item.AbstractItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.*;

public class Item extends AbstractItem implements TerraItem {
    private final String name;
    @Nullable
    private BuffComponent buffComponent;
    @Nullable
    private CommandComponent commandComponent;
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
    @Nullable
    private SlotComponent slotComponent;
    /**
     * 依据内部名称和对应的config文件创建mc基础物品
     */
    public Item(String id, ConfigurationSection cfg) {
        super(cfg);
        this.name = id;

        ConfigurationSection sub;
        if (cfg.isSet("buff")) {
            sub = cfg.getConfigurationSection("buff");
            if (sub != null) {
                buffComponent = new BuffComponent(sub);
                buffComponent.apply(bukkitItem);
            }
        }
        if (cfg.isSet("command")) {
            sub = cfg.getConfigurationSection("command");
            if (sub != null) {
                commandComponent = new CommandComponent(sub);
                commandComponent.apply(bukkitItem);
            }
        }
        if (cfg.isSet("damage_type")) {
            sub = cfg.getConfigurationSection("damage_type");
            if (sub != null) {
                damageTypeComponent = new DamageTypeComponent(sub);
                damageTypeComponent.apply(bukkitItem);
            }
        }
        if (cfg.isSet("terra_durability")) {
            sub = cfg.getConfigurationSection("terra_durability");
            if (sub != null) {
                durabilityComponent = new DurabilityComponent(sub);
                durabilityComponent.apply(bukkitItem);
            }
        }
        if (cfg.isSet("gem")) {
            sub = cfg.getConfigurationSection("gem");
            if (sub != null) {
                gemComponent = new GemComponent(sub);
                gemComponent.apply(bukkitItem);
            }
        }
        if (cfg.isSet("gem_holder")) {
            sub = cfg.getConfigurationSection("gem_holder");
            if (sub != null) {
                gemHolderComponent = new GemHolderComponent(sub);
                gemHolderComponent.apply(bukkitItem);
            }
        }
        if (cfg.isSet("level")) {
            sub = cfg.getConfigurationSection("level");
            if (sub != null) {
                levelComponent = new LevelComponent(sub);
                levelComponent.apply(bukkitItem);
            }
        }
        if (cfg.isSet("meta")) {
            sub = cfg.getConfigurationSection("meta");
            if (sub != null) {
                metaComponent = new MetaComponent(sub);
                metaComponent.apply(bukkitItem);
            }
        }
        if (cfg.isSet("quality")) {
            sub = cfg.getConfigurationSection("quality");
            if (sub != null) {
                qualityComponent = new QualityComponent(sub);
                qualityComponent.apply(bukkitItem);
            }
        }
        if (cfg.isSet("skill")) {
            sub = cfg.getConfigurationSection("skill");
            if (sub != null) {
                skillComponent = new SkillComponent(sub);
                skillComponent.apply(bukkitItem);
            }
        }
        if (cfg.isSet("slot")) {
            sub = cfg.getConfigurationSection("slot");
            if (sub != null) {
                slotComponent = new SlotComponent(sub);
                slotComponent.apply(bukkitItem);
            }
        }

        terraNameComponent = new TerraNameComponent(id);
        terraNameComponent.apply(bukkitItem);
        updateCodeComponent = new UpdateCodeComponent(this.hashCode());
        updateCodeComponent.apply(bukkitItem);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean updateOld(ItemStack old) {
        if (UpdateCodeComponent.from(old).getCode() == this.updateCodeComponent.getCode()) return false;
        super.updateOld(old);
        /* innerName 不能被更改 */
        /* updateCode 必须被更改 */
        updateCodeComponent.apply(old);
        if (buffComponent != null && buffComponent.canUpdate()) buffComponent.updatePartial().apply(old);
        if (commandComponent != null && commandComponent.canUpdate()) commandComponent.updatePartial().apply(old);
        if (damageTypeComponent != null && damageTypeComponent.canUpdate()) damageTypeComponent.updatePartial().apply(old);
        if (durabilityComponent != null && durabilityComponent.canUpdate()) durabilityComponent.updatePartial().apply(old);
        if (gemComponent != null && gemComponent.canUpdate()) gemComponent.updatePartial().apply(old);
        if (gemHolderComponent != null && gemHolderComponent.canUpdate()) gemHolderComponent.updatePartial().apply(old);
        if (levelComponent != null && levelComponent.canUpdate()) levelComponent.updatePartial().apply(old);
        if (metaComponent != null && metaComponent.canUpdate()) metaComponent.updatePartial().apply(old);
        if (qualityComponent != null && qualityComponent.canUpdate()) qualityComponent.updatePartial().apply(old);
        if (skillComponent != null && skillComponent.canUpdate()) skillComponent.updatePartial().apply(old);
        if (slotComponent != null && slotComponent.canUpdate()) slotComponent.updatePartial().apply(old);
        return true;
    }

    @Override
    @Nullable
    public TerraBuffComponent getBuffComponent() {
        return buffComponent;
    }

    @Override
    @Nullable
    public TerraCommandsComponent getCommandComponent() {
        return commandComponent;
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
    @Nullable
    public TerraMetaSlotComponent getMetaSlotComponent() {
        return slotComponent;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                super.hashCode(), name, terraNameComponent, buffComponent,
                commandComponent, damageTypeComponent, durabilityComponent, gemComponent, gemHolderComponent,
                levelComponent, metaComponent, qualityComponent, skillComponent, slotComponent
        );
    }
}
