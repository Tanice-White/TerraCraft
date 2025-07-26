package io.github.tanice.terraCraft.core.config;

import io.github.tanice.terraCraft.api.config.TerraConfigManager;
import io.github.tanice.terraCraft.api.plugin.TerraPlugin;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.stream.Stream;

public final class ConfigManager implements TerraConfigManager {
    private final TerraPlugin plugin;

    private static final String RESOURCE_FOLDER = "/config/";

    private double version;
    private boolean debug;
    private boolean generateExamples;
    private boolean cancelGenericParticles;
    private boolean generateDamageIndicator;
    private String defaultPrefix;
    private String criticalPrefix;
    private double criticalLargeScale;
    private double viewRange;
    private double worldK;
    private boolean damageFloat;
    private double damageFloatRange;
    private boolean useDamageReductionBalanceForPlayer;
    private double originalCriticalStrikeAddition;
    private boolean useMysql;
    private String host;
    private String port;
    private String database;
    private String username;
    private String password;
    private double originalMaxHealth;
    private double originalMaxMana;
    private double originalManaRecoverySpeed;
    private double rarity_intensity;

    public ConfigManager(TerraPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    public void reload() {
        load();
    }

    public void unload() {

    }

    @Override
    public double getVersion() {
        return this.version;
    }

    @Override
    public boolean isDebug() {
        return this.debug;
    }

    @Override
    public boolean shouldGenerateExamples() {
        return this.generateExamples;
    }

    @Override
    public boolean shouldCancelGenericParticles() {
        return this.cancelGenericParticles;
    }

    @Override
    public boolean shouldGenerateDamageIndicator() {
        return this.generateDamageIndicator;
    }

    @Override
    public String getDefaultPrefix() {
        return this.defaultPrefix;
    }

    @Override
    public String getCriticalPrefix() {
        return this.criticalPrefix;
    }

    @Override
    public double getCriticalLargeScale() {
        return this.criticalLargeScale;
    }

    @Override
    public double getViewRange() {
        return this.viewRange;
    }

    @Override
    public double getWorldK() {
        return this.worldK;
    }

    @Override
    public boolean isDamageFloatEnabled() {
        return this.damageFloat;
    }

    @Override
    public double getDamageFloatRange() {
        return this.damageFloatRange;
    }

    @Override
    public boolean useDamageReductionBalanceForPlayer() {
        return this.useDamageReductionBalanceForPlayer;
    }

    @Override
    public double getOriginalCriticalStrikeAddition() {
        return this.originalCriticalStrikeAddition;
    }

    @Override
    public boolean useMysql() {
        return this.useMysql;
    }

    @Override
    public void setUseMysql(boolean useMysql) {
        this.useMysql = useMysql;
    }

    @Override
    public String getHost() {
        return this.host;
    }

    @Override
    public String getPort() {
        return this.port;
    }

    @Override
    public String getDatabase() {
        return this.database;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public double getOriginalMaxHealth() {
        return this.originalMaxHealth;
    }

    @Override
    public double getOriginalMaxMana() {
        return this.originalMaxMana;
    }

    @Override
    public double getOriginalManaRecoverySpeed() {
        return this.originalManaRecoverySpeed;
    }

    @Override
    public double getRarityIntensity() {
        return this.rarity_intensity;
    }

    @Override
    public void saveDefaultConfig() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()){
            generateExampleConfig();
            return;
        }
        if (generateExamples) generateExampleConfig();
    }

    public synchronized void load() {
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "config.yml"));
        ConfigurationSection sub;

        version = cfg.getDouble("VERSION", -1);
        debug = cfg.getBoolean("DEBUG", false);
        generateExamples = cfg.getBoolean("generate_examples", true);
        cancelGenericParticles = cfg.getBoolean("cancel_generic_particles", false);
        generateDamageIndicator = cfg.getBoolean("generate_damage_indicator", false);
        defaultPrefix = cfg.getString("default_prefix", "§6");
        criticalPrefix = cfg.getString("critical_prefix", "§4");
        criticalLargeScale = cfg.getDouble("critical_large_scale", 1D);
        viewRange = cfg.getDouble("view_range", 20D);
        worldK = cfg.getDouble("world_k", 1D);
        damageFloat = cfg.getBoolean("damage_float", false);
        damageFloatRange = cfg.getDouble("damage_float_range", 0D);
        useDamageReductionBalanceForPlayer = cfg.getBoolean("use_damage_reduction_balance_for_player", false);
        originalCriticalStrikeAddition = cfg.getDouble("original_critical_strike_addition", 0.2D);

        sub = cfg.getConfigurationSection("database");
        if (sub == null) {
            TerraCraftLogger.error("Global configuration file error, unable to connect to database");
            useMysql = false;
        } else {
            useMysql = sub.getBoolean("use_mysql", false);
            sub = sub.getConfigurationSection("mysql");
            if (sub != null) {
                host = sub.getString("host", "localhost");
                port = sub.getString("port", "3306");
                database = sub.getString("database_name");
                username = sub.getString("username");
                password = sub.getString("password");
            }
            if (useMysql && sub == null) {
                TerraCraftLogger.error("Global configuration file error, unable to connect to database");
                useMysql = false;
            }
        }

        originalMaxHealth = cfg.getDouble("original_max_health", 20D);
        originalMaxMana = cfg.getDouble("original_max_mana", 50D);
        originalManaRecoverySpeed = cfg.getDouble("original_mana_recovery_speed", 0.4D);
        rarity_intensity = cfg.getDouble("rarity_intensity", 0.5D);
    }

    private void generateExampleConfig() {
        File targetFolder = plugin.getDataFolder();
        URL sourceUrl = plugin.getClass().getResource("");
        if (sourceUrl == null) {
            TerraCraftLogger.error("The plugin package is incomplete, please re_download it!");
            return;
        }

        try (FileSystem fs = FileSystems.newFileSystem(sourceUrl.toURI(), Collections.emptyMap())) {
            Path cp = fs.getPath(RESOURCE_FOLDER);
            try (Stream<Path> sourcePaths = Files.walk(cp)) {
                for (Path source : sourcePaths.toArray(Path[]::new)) {
                    Path targetPath = targetFolder.toPath().resolve(cp.relativize(source).toString());
                    if (Files.exists(targetPath)) continue;
                    if (Files.isDirectory(source)) Files.createDirectory(targetPath);
                    else Files.copy(source, targetPath);
                }
                TerraCraftLogger.success("Example config files generated successfully!");
            }
        } catch (IOException | URISyntaxException e) {
            TerraCraftLogger.error("Failed to load default example config file: " + e.getMessage());
        }
    }
}
