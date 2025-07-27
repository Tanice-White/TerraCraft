package io.github.tanice.terraCraft.core.items;

import io.github.tanice.terraCraft.api.items.TerraItemManager;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.levels.TerraLevel;
import io.github.tanice.terraCraft.api.items.qualities.TerraQualityGroup;
import io.github.tanice.terraCraft.api.plugin.TerraPlugin;
import io.github.tanice.terraCraft.core.items.levels.LevelTemplate;
import io.github.tanice.terraCraft.core.items.qualities.QualityGroup;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.tanice.terraCraft.core.constants.DataFolders.*;

public final class ItemManager implements TerraItemManager {

    private final TerraPlugin plugin;

    private final ItemProvider provider;
    private final ConcurrentMap<String, TerraBaseItem> items;

    private final ConcurrentMap<String, TerraLevel> levelTemplates;
    private final ConcurrentMap<String, TerraQualityGroup> qualityGroups;

    public ItemManager(TerraPlugin plugin) {
        this.plugin = plugin;
        this.provider = new ItemProvider();
        this.items = new ConcurrentHashMap<>();
        this.levelTemplates = new ConcurrentHashMap<>();
        this.qualityGroups = new ConcurrentHashMap<>();
        this.loadResource();
        this.loadLevelTemplates();
        this.loadQualityGroups();
    }

    public void reload() {
    }

    public void unload() {
    }

    @Override
    public Collection<String> getItemNames() {
        return Collections.unmodifiableCollection(items.keySet());
    }

    @Override
    public Optional<TerraBaseItem> getItem(String name) {
        if (name == null) return Optional.empty();
        return Optional.ofNullable(items.get(name));
    }

    @Override
    public boolean isTerraItem(String name) {
        return items.containsKey(name);
    }

    @Override
    public Collection<String> filterItems(Collection<TerraBaseItem> items, String name) {
        if (name == null) return getItemNames();
        if (items == null || items.isEmpty()) return Collections.emptyList();
        return items.stream()
                .filter(item -> item.getName() != null && item.getName().startsWith(name))
                .map(TerraBaseItem::getName)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<TerraLevel> getLevelTemplate(String name) {
        return Optional.ofNullable(levelTemplates.get(name));
    }

    @Override
    public Optional<TerraQualityGroup> getQualityGroup(String name) {
        return Optional.ofNullable(qualityGroups.get(name));
    }

    private void loadResource() {
        Path itemDir = plugin.getDataFolder().toPath().resolve(ITEM_FOLDER);
        if (!Files.exists(itemDir) || !Files.isDirectory(itemDir)) {
            TerraCraftLogger.error("Items directory validation failed: " + itemDir + " is not a valid directory");
            return;
        }
        try (Stream<Path> files = Files.list(itemDir)) {
            files.forEach(file -> {
                String fileName = file.getFileName().toString();
                if (fileName.endsWith(".yml")) {
                    ConfigurationSection section = YamlConfiguration.loadConfiguration(file.toFile());
                    for (String k : section.getKeys(false)) {
                        if (items.containsKey(k)) {
                            TerraCraftLogger.error("Existing item: " + k);
                            continue;
                        }
                        provider.createItem(k, section.getConfigurationSection(k)).ifPresent(b -> items.put(k, b));
                    }
                }
            });
            TerraCraftLogger.success("Loaded " + provider.getTotal() + " items in total, including " + provider.getValid() + " valid items and " + provider.getOther() + " invalid type items.");
        } catch (IOException e) {
            TerraCraftLogger.error("Failed to load buffs from " + itemDir.toAbsolutePath() + " " + e.getMessage());
        }
    }

    private void loadLevelTemplates() {
        Path levelDir = plugin.getDataFolder().toPath().resolve(LEVEL_FOLDER);
        if (!Files.exists(levelDir) || !Files.isDirectory(levelDir)) {
            TerraCraftLogger.error("Levels directory validation failed: " + levelDir + " is not a valid directory");
            return;
        }
        AtomicInteger num = new AtomicInteger();
        try (Stream<Path> files = Files.list(levelDir)) {
            files.forEach(file -> {
                String fileName = file.getFileName().toString();
                if (fileName.endsWith(".yml")) {
                    ConfigurationSection section = YamlConfiguration.loadConfiguration(file.toFile());
                    for (String k : section.getKeys(false)) {
                        if (levelTemplates.containsKey(k)) {
                            TerraCraftLogger.error("Existing level template: " + k);
                            continue;
                        }
                        ConfigurationSection sub = section.getConfigurationSection(k);
                        if (sub == null) {
                            TerraCraftLogger.error("Empty level template: " + k);
                            continue;
                        }
                        levelTemplates.put(k, new LevelTemplate(k, sub));
                        num.getAndIncrement();
                    }
                }
            });
            TerraCraftLogger.success("Loaded " + num + " level template in total");
        } catch (IOException e) {
            TerraCraftLogger.error("Failed to load level template from " + levelDir.toAbsolutePath() + " " + e.getMessage());
        }
    }

    private void loadQualityGroups() {
        Path qualityDir = plugin.getDataFolder().toPath().resolve(QUALITY_FOLDER);
        if (!Files.exists(qualityDir) || !Files.isDirectory(qualityDir)) {
            TerraCraftLogger.error("Qualities directory validation failed: " + qualityDir + " is not a valid directory");
            return;
        }
        AtomicInteger num = new AtomicInteger();
        try (Stream<Path> files = Files.list(qualityDir)) {
            files.forEach(file -> {
                String fileName = file.getFileName().toString();
                if (fileName.endsWith(".yml")) {
                    ConfigurationSection section = YamlConfiguration.loadConfiguration(file.toFile());
                    for (String k : section.getKeys(false)) {
                        if (qualityGroups.containsKey(k)) {
                            TerraCraftLogger.error("Existing quality group: " + k);
                            continue;
                        }
                        ConfigurationSection sub = section.getConfigurationSection(k);
                        if (sub == null) {
                            TerraCraftLogger.error("Empty quality group: " + k);
                            continue;
                        }
                        qualityGroups.put(k, new QualityGroup(k, sub));
                        num.getAndIncrement();
                    }
                }
            });
            TerraCraftLogger.success("Loaded " + num + " quality groups in total");
        } catch (IOException e) {
            TerraCraftLogger.error("Failed to load quality group from " + qualityDir.toAbsolutePath() + " " + e.getMessage());
        }
    }
}
