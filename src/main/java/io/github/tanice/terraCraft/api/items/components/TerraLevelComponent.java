package io.github.tanice.terraCraft.api.items.components;

import javax.annotation.Nullable;

public interface TerraLevelComponent extends TerraBaseComponent {

    int getLevel();

    void setLevel(int level);

    @Nullable String getTemplate();

    void setTemplate(String template);
}
