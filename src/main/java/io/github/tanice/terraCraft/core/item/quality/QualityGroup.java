package io.github.tanice.terraCraft.core.item.quality;

import io.github.tanice.terraCraft.api.item.quality.TerraQuality;
import io.github.tanice.terraCraft.api.item.quality.TerraQualityGroup;
import io.github.tanice.terraCraft.core.config.ConfigManager;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

import static io.github.tanice.terraCraft.core.constant.ConfigKeys.*;

/**
 * 品质组类，用于管理一组品质并实现基于权重的随机选择
 * 核心特性：权重越低的品质稀有度越高，出现概率越低
 */
public class QualityGroup implements TerraQualityGroup {
    private final Random rand;
    private final String name;
    /** 按权重升序排列的品质列表 */
    private final List<TerraQuality> qualities;
    /** 稀有度乘数数组 */
    private final double[] rarityMultipliers;
    /** 控制稀有度差异的强度（数值越大，低权重与高权重的概率差距越悬殊） */
    private final double rarityIntensity;
    /** 调整后权重前缀和 */
    private final double[] adjustedPrefixWeights;
    /** 调整后的总权重（应用稀有度乘数后的总和） */
    private final double totalAdjustedWeight;

    public QualityGroup(String name, ConfigurationSection cfg) {
        this.rand = new Random();
        this.name = name;
        this.rarityIntensity = ConfigManager.getRarityIntensity();

        this.qualities = new ArrayList<>();
        this.loadResource(cfg);
        /* 按权重升序 */
        this.qualities.sort(Comparator.comparingInt(TerraQuality::getOriWeight));
        this.rarityMultipliers = calculateRarityMultipliers();
        this.adjustedPrefixWeights = calculateAdjustedPrefixWeights();
        this.totalAdjustedWeight = adjustedPrefixWeights.length > 0 ? adjustedPrefixWeights[adjustedPrefixWeights.length - 1] : 0;
    }

    /**
     * 随机选择品质（严格遵循：权重越低，概率越低，稀有度越高）
     * @return 选中的品质，若组为空则返回null
     */
    @Override
    public TerraQuality randomSelect() {
        if (qualities.isEmpty() || totalAdjustedWeight <= 0) {
            return null;
        }
        /* 生成调整后权重范围内的随机数 */
        double random = rand.nextDouble() * totalAdjustedWeight;
        return qualities.get(findAdjustedIndex(random));
    }

    /**
     * 输出稀有度分布日志，用于调试和验证概率分布
     */
    @Deprecated
    public void getView() {
        System.out.println("Quality group [" + name + "] 稀有度分布（权重越低越稀有）：");

        for (int i = 0; i < qualities.size(); i++) {
            TerraQuality quality = qualities.get(i);
            int weight = quality.getOriWeight();
            double adjustedWeight = weight * rarityMultipliers[i];
            double probability = (adjustedWeight / totalAdjustedWeight) * 100;

            System.out.printf("  %s: 权重=%d, 稀有度乘数=%.3f, 实际概率=%.2f%%%n",
                    quality.getName(), weight, rarityMultipliers[i], probability);
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<TerraQuality> getQualities() {
        return Collections.unmodifiableList(this.qualities);
    }

    @Override
    public double getQualitySize() {
        return this.qualities.size();
    }

    private void loadResource(ConfigurationSection cfg) {
        ConfigurationSection sc;
        String displayName;
        int weight;
        for (String key : cfg.getKeys(false)) {
            sc = cfg.getConfigurationSection(key);
            if (sc == null) continue;

            displayName = sc.getString(DISPLAY_NAME, key);
            if (!sc.isSet(WEIGHT)) {
                TerraCraftLogger.error("Quality " + key + " in Group " + this.name + " is missing [weight] value! Skipped.");
                continue;
            }
            weight = sc.getInt(WEIGHT);
            this.qualities.add(new Quality(key , weight, displayName, sc));
        }
    }

    /**
     * 计算稀有度乘数
     * 索引越小（权重越低/越稀有），乘数越小，概率被压制得越低
     * 公式：乘数 = e^(-稀有度强度 × 索引)，实现指数级衰减
     * @return 稀有度乘数数组
     */
    private double[] calculateRarityMultipliers() {
        int size = qualities.size();
        double[] multipliers = new double[size];
        for (int i = 0; i < size; i++) {
            multipliers[i] = Math.exp(-rarityIntensity * i);
        }
        return multipliers;
    }

    /**
     * 计算调整后的权重前缀和数组
     * @return 调整后的前缀和数组
     */
    private double[] calculateAdjustedPrefixWeights() {
        int size = qualities.size();
        double[] prefixWeights = new double[size];
        double cumulative = 0;
        for (int i = 0; i < size; i++) {
            cumulative += qualities.get(i).getOriWeight() * rarityMultipliers[i];
            prefixWeights[i] = cumulative;
        }

        return prefixWeights;
    }

    /**
     * 二分查找
     * @param random 随机数
     * @return 对应的品质索引
     */
    private int findAdjustedIndex(double random) {
        int left = 0;
        int right = adjustedPrefixWeights.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (adjustedPrefixWeights[mid] > random) right = mid;
            else left = mid + 1;
        }
        return left;
    }
}
