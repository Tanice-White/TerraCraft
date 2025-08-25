package io.github.tanice.terraCraft.api.item.level;

import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;

public interface TerraLevelTemplate {

    String getName();

    int getBegin();

    int getMax();

    double getChance();

    String getMaterial();

    boolean isFailedLevelDown();

    /**
     * 获取模板属性的可变副本
     */
    TerraCalculableMeta getMeta();
}
