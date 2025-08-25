package io.github.tanice.terraCraft.api.item.component;

import javax.annotation.Nullable;

public interface TerraQualityComponent extends TerraBaseComponent {

    @Nullable String getQuality();

    void setQuality(@Nullable String quality);

    @Nullable String getQualityGroup();

    void setQualityGroup(@Nullable String group);
}
