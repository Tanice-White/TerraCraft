package io.github.tanice.terraCraft.api.items;

import io.github.tanice.terraCraft.api.items.components.*;

import javax.annotation.Nullable;

public interface TerraItem extends TerraBaseItem {

    @Nullable
    TerraBuffComponent getBuffComponent();

    @Nullable
    TerraCommandsComponent getCommandComponent();

    @Nullable
    TerraDamageTypeComponent getDamageTypeComponent();

    @Nullable
    TerraDurabilityComponent getDurabilityComponent();

    @Nullable
    TerraGemComponent getGemComponent();

    @Nullable
    TerraGemHolderComponent getGemHolderComponent();

    TerraInnerNameComponent getInnerNameComponent();

    @Nullable
    TerraLevelComponent getLevelComponent();

    @Nullable
    TerraMetaComponent getMetaComponent();

    @Nullable
    TerraQualityComponent getQualityComponent();

    @Nullable
    TerraSkillComponent getSkillComponent();

    TerraUpdateCodeComponent getUpdateCodeComponent();
}
