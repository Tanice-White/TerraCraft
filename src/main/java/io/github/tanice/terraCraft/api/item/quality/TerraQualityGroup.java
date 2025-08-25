package io.github.tanice.terraCraft.api.item.quality;

import java.util.List;

public interface TerraQualityGroup {

    TerraQuality randomSelect();

    String getName();

    List<TerraQuality> getQualities();

    double getQualitySize();
}
