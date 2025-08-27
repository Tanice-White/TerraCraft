package io.github.tanice.terraCraft.api.item.quality;

import java.util.List;

public interface TerraQualityGroup {

    /**
     * 合并多个品质组为一个新的品质组 - 值均为引用, 会修改调用方
     * 使用全新的值逐个merge
     * @param groups 要合并的品质组
     */
    void merge(TerraQualityGroup... groups);

    /**
     * 随机选择品质（严格遵循：权重越低，概率越低，稀有度越高）
     * @return 选中的品质，若组为空则返回null
     */
    TerraQuality randomSelect();

    String getName();

    /**
     * 获取所有品质的引用
     */
    List<TerraQuality> getQualities();

    double getQualitySize();
}
