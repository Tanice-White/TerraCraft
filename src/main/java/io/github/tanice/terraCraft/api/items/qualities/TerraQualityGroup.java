package io.github.tanice.terraCraft.api.items.qualities;

import java.util.List;

public interface TerraQualityGroup {

    TerraQuality randomSelect();

    String getName();

    List<TerraQuality> getQualities();

    double getQualitySize();
}
