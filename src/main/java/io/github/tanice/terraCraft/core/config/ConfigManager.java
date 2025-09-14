package io.github.tanice.terraCraft.core.config;

import io.github.tanice.terraCraft.api.attribute.AttributeActiveSection;
import io.github.tanice.terraCraft.api.attribute.AttributeType;
import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.core.attribute.CalculableMeta;
import io.github.tanice.terraCraft.core.util.logger.TerraCraftLogger;
import io.github.tanice.terraCraft.core.util.registry.Registry;
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

import static io.github.tanice.terraCraft.core.util.EnumUtil.safeValueOf;

public final class ConfigManager {

    private static final String RESOURCE_FOLDER = "/config/";

    private static double version;
    private static boolean debug;
    private static boolean generateExamples;
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
    private static double rarityIntensity;
    private static Map<String, Boolean> oriUpdateConfigMap;

    private static double originalExternalHealth;
    private static double originalMaxMana;
    private static TerraCalculableMeta originalPlayerMeta;

    public static void load() {
        File configFile = new File(TerraCraftBukkit.inst().getDataFolder(), "config.yml");
        if (!configFile.exists())generateExampleConfig();
        else if (generateExamples) generateExampleConfig();

        loadRegistry();

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(configFile);
        ConfigurationSection sub;

        version = cfg.getDouble("VERSION", -1);
        debug = cfg.getBoolean("DEBUG", false);
        generateExamples = cfg.getBoolean("generate_examples", true);
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
        rarityIntensity = cfg.getDouble("rarity_intensity", 0.5D);
        oriUpdateConfigMap = new HashMap<>();
        sub = cfg.getConfigurationSection("update");
        if (sub == null) TerraCraftLogger.error("Global configuration file error, there is no update config section");
        else for (String key : sub.getKeys(false)) oriUpdateConfigMap.put(key, sub.getBoolean(key, false));

        sub = cfg.getConfigurationSection("player_original_attribute");
        if (sub == null) TerraCraftLogger.error("Global configuration file error, there is no player original attribute section");
        else {
            originalExternalHealth = sub.getDouble("external_health", 20D);
            originalMaxMana = sub.getDouble("max_mana", 50D);
            originalPlayerMeta = new CalculableMeta(sub.getConfigurationSection("attribute"), safeValueOf(AttributeActiveSection.class, sub.getString("section"), AttributeActiveSection.BASE));
        }
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

    public static float getOriginalExternalMaxHealth() {
        return (float) originalExternalHealth;
    }

    public static double getOriginalMaxMana() {
        return originalMaxMana;
    }

    public static TerraCalculableMeta getOriginalPlayerMeta() {
        return originalPlayerMeta;
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

    private static void loadRegistry() {
        File configFile = new File(TerraCraftBukkit.inst().getDataFolder(), "vanilla.yml");
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(configFile);
        ConfigurationSection tmp;
        ConfigurationSection sub = cfg.getConfigurationSection("item");
        int total = 0;
        if (sub != null) {
            for (String key : sub.getKeys(false)) {
                tmp = sub.getConfigurationSection(key);
                if (tmp == null) continue;
                Registry.ORI_ITEM.register(key.toLowerCase(), new CalculableMeta(tmp.getConfigurationSection("attribute"), safeValueOf(AttributeActiveSection.class, tmp.getString("section"), AttributeActiveSection.BASE)));
                total ++;
            }
        }
        TerraCraftLogger.success("[Registry] Loaded " + total + " ORI_ITEM");
        total = 0;
        sub = cfg.getConfigurationSection("potion");
        if (sub != null) {
            for (String key : sub.getKeys(false)) {
                tmp = sub.getConfigurationSection(key);
                if (tmp == null) continue;
                Registry.ORI_POTION.register(key.toLowerCase(), new CalculableMeta(tmp.getConfigurationSection("attribute"), safeValueOf(AttributeActiveSection.class, tmp.getString("section"), AttributeActiveSection.BASE)));
                total ++;
            }
        }
        TerraCraftLogger.success("[Registry] Loaded " + total + " ORI_POTION");
        total = 0;
        sub = cfg.getConfigurationSection("enchant");
        if (sub != null) {
            for (String key : sub.getKeys(false)) {
                tmp = sub.getConfigurationSection(key);
                if (tmp == null) continue;
                Registry.ORI_ENCHANT.register(key, new CalculableMeta(tmp, safeValueOf(AttributeActiveSection.class, tmp.getString("section"), AttributeActiveSection.BASE)));
                total ++;
            }
        }
        TerraCraftLogger.success("[Registry] Loaded " + total + " ORI_ENCHANT");
        total = 0;
        sub = cfg.getConfigurationSection("living_entity");
        if (sub != null) {
            for (String key : sub.getKeys(false)) {
                tmp = sub.getConfigurationSection(key);
                if (tmp == null) continue;
                Registry.ORI_LIVING_ENTITY.register(key.toLowerCase(), new CalculableMeta(tmp.getConfigurationSection("attribute"), safeValueOf(AttributeActiveSection.class, tmp.getString("section"), AttributeActiveSection.BASE)));
                total ++;
            }
        }
        TerraCraftLogger.success("[Registry] Loaded " + total + " ORI_LIVING_ENTITY");
    }
}
