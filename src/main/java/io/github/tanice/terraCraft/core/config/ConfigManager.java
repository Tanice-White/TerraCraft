package io.github.tanice.terraCraft.core.config;

import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
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
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public final class ConfigManager {

    private static final String RESOURCE_FOLDER = "/config/";

    private static double version;
    private static boolean debug;
    private static boolean generateExamples;
    private static boolean generateDamageIndicator;
    private static String defaultPrefix;
    private static String criticalPrefix;
    private static double criticalLargeScale;
    private static double viewRange;
    private static double worldK;
    private static boolean damageFloat;
    private static double damageFloatRange;
    private static boolean useDamageReductionBalanceForPlayer;
    private static double originalCriticalStrikeAddition;
    private static boolean useMysql;
    private static String host;
    private static String port;
    private static String database;
    private static String username;
    private static String password;
    private static double originalMaxHealth;
    private static double originalMaxMana;
    private static double originalManaRecoverySpeed;
    private static double rarityIntensity;
    private static Map<String, Boolean> oriUpdateConfigMap;

    public static synchronized void load() {
        File configFile = new File(TerraCraftBukkit.inst().getDataFolder(), "config.yml");
        if (!configFile.exists())generateExampleConfig();
        else if (generateExamples) generateExampleConfig();

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(configFile);
        ConfigurationSection sub;

        version = cfg.getDouble("VERSION", -1);
        debug = cfg.getBoolean("DEBUG", false);
        generateExamples = cfg.getBoolean("generate_examples", true);
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
        rarityIntensity = cfg.getDouble("rarity_intensity", 0.5D);
        oriUpdateConfigMap = new HashMap<>();
        sub = cfg.getConfigurationSection("update");
        if (sub == null) TerraCraftLogger.error("Global configuration file error, there is no update config section");
        else for (String key : sub.getKeys(false)) oriUpdateConfigMap.put(key, sub.getBoolean(key, false));
    }

    public void reload() {
        load();
    }

    public void unload() {

    }

    public static double getVersion() {
        return version;
    }

    public static boolean isDebug() {
        return debug;
    }

    public static boolean shouldGenerateExamples() {
        return generateExamples;
    }

    public static boolean shouldGenerateDamageIndicator() {
        return generateDamageIndicator;
    }

    public static String getDefaultPrefix() {
        return defaultPrefix;
    }

    public static String getCriticalPrefix() {
        return criticalPrefix;
    }

    public static double getCriticalLargeScale() {
        return criticalLargeScale;
    }

    public static double getViewRange() {
        return viewRange;
    }

    public static double getWorldK() {
        return worldK;
    }

    public static boolean isDamageFloatEnabled() {
        return damageFloat;
    }

    public static double getDamageFloatRange() {
        return damageFloatRange;
    }

    public static boolean useDamageReductionBalanceForPlayer() {
        return useDamageReductionBalanceForPlayer;
    }

    public static double getOriginalCriticalStrikeAddition() {
        return originalCriticalStrikeAddition;
    }

    public static boolean useMysql() {
        return useMysql;
    }

    public static void setUseMysql(boolean use) {
        useMysql = use;
    }

    public static String getHost() {
        return host;
    }

    public static String getPort() {
        return port;
    }

    public static String getDatabase() {
        return database;
    }

    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }

    public static double getOriginalMaxHealth() {
        return originalMaxHealth;
    }

    public static double getOriginalMaxMana() {
        return originalMaxMana;
    }

    public static double getOriginalManaRecoverySpeed() {
        return originalManaRecoverySpeed;
    }

    public static double getRarityIntensity() {
        return rarityIntensity;
    }

    public static Map<String, Boolean> getOriUpdateConfigMap() {
        return oriUpdateConfigMap;
    }

    private static void generateExampleConfig() {
        File targetFolder = TerraCraftBukkit.inst().getDataFolder();
        URL sourceUrl = TerraCraftBukkit.inst().getClass().getResource("");
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
