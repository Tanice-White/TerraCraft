package io.github.tanice.terraCraft.api.config;

public interface TerraConfigManager {
    double getVersion();

    boolean isDebug();

    boolean shouldGenerateExamples();

    boolean shouldCancelGenericParticles();

    boolean shouldGenerateDamageIndicator();

    String getDefaultPrefix();

    String getCriticalPrefix();

    double getCriticalLargeScale();

    double getViewRange();

    double getWorldK();

    boolean isDamageFloatEnabled();

    double getDamageFloatRange();

    boolean useDamageReductionBalanceForPlayer();

    double getOriginalCriticalStrikeAddition();

    boolean useMysql();

    void setUseMysql(boolean useMysql);

    String getHost();

    String getPort();

    String getDatabase();

    String getUsername();

    String getPassword();

    double getOriginalMaxHealth();

    double getOriginalMaxMana();

    double getOriginalManaRecoverySpeed();

    double getRarityIntensity();

    void saveDefaultConfig();
}
