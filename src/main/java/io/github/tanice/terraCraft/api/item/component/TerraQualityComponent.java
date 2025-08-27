package io.github.tanice.terraCraft.api.item.component;

import javax.annotation.Nullable;
import java.util.List;

public interface TerraQualityComponent extends TerraBaseComponent {

    @Nullable String getQuality();

    void setQuality(@Nullable String quality);

    List<String> getGroups();

    void setGroups(@Nullable List<String> group);
}
