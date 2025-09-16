package io.github.tanice.terraCraft.core.item;

import io.github.tanice.terraCraft.api.item.TerraItemManager;
import io.github.tanice.terraCraft.api.item.TerraBaseItem;
import io.github.tanice.terraCraft.api.item.level.TerraLevelTemplate;
import io.github.tanice.terraCraft.api.item.quality.TerraQuality;
import io.github.tanice.terraCraft.api.item.quality.TerraQualityGroup;
import io.github.tanice.terraCraft.api.plugin.TerraPlugin;
import io.github.tanice.terraCraft.core.item.level.LevelTemplate;
import io.github.tanice.terraCraft.core.item.quality.QualityGroup;
import io.github.tanice.terraCraft.core.util.logger.TerraCraftLogger;
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

import static io.github.tanice.terraCraft.core.constant.DataFolders.*;

public final class ItemManager implements TerraItemManager {

    private final TerraPlugin plugin;

    private final ItemProvider provider;
    private final ConcurrentMap<String, TerraBaseItem> items;

    private final ConcurrentMap<String, TerraLevelTemplate> levelTemplates;
    private final ConcurrentMap<String, TerraQualityGroup> qualityGroups;
    private final ConcurrentMap<String, TerraQuality> qualities;

    public ItemManager(TerraPlugin plugin) {
        this.plugin = plugin;
        this.provider = new ItemProvider();
        this.items = new ConcurrentHashMap<>();
        this.levelTemplates = new ConcurrentHashMap<>();
        this.qualityGroups = new ConcurrentHashMap<>();
        this.qualities = new ConcurrentHashMap<>();
        this.loadLevelTemplates();
        this.loadQualityGroups();
        this.loadResource();
    }

    public void reload() {
        provider.reload();
        items.clear();
        levelTemplates.clear();
        qualityGroups.clear();
        qualities.clear();
        loadLevelTemplates();
        loadQualityGroups();
        loadResource();
    }

    public void unload() {
        this.items.clear();
        this.levelTemplates.clear();
        this.qualityGroups.clear();
        this.qualities.clear();
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
    public Collection<String> filterItems(String name) {
        if (name == null) return getItemNames();
        return items.values().stream()
                .filter(item -> item.getName() != null && item.getName().startsWith(name))
                .map(TerraBaseItem::getName)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<String> filterTemplates(String name) {
        return levelTemplates.keySet()
                .stream().filter(tmp -> tmp.startsWith(name))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<String> filterQualityGroups(String name) {
        return qualityGroups.keySet()
                .stream().filter(tmp -> tmp.startsWith(name))
                .toList();
    }

    @Override
    public Collection<String> filterQualities(String name) {
        return qualities.keySet().stream().filter(tmp -> tmp.startsWith(name)).toList();
    }

    @Override
    public Optional<TerraLevelTemplate> getLevelTemplate(String name) {
        return Optional.ofNullable(levelTemplates.get(name));
    }

    @Override
    public Optional<TerraQualityGroup> getQualityGroup(String name) {
        return Optional.ofNullable(qualityGroups.get(name));
    }

    @Override
    public Optional<TerraQuality> getQuality(String name) {
        return Optional.ofNullable(qualities.get(name));
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
            TerraCraftLogger.success("Loaded " + provider.getTotal() + " items");
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
            TerraCraftLogger.success("Loaded " + num + " level templates");
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
                TerraQualityGroup group;
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
                        group = new QualityGroup(k, sub);
                        qualityGroups.put(k, group);
                        for (TerraQuality q : group.getQualities()) {
                            if (qualities.containsKey(q.getName())) {
                                TerraCraftLogger.warning("Duplicate quality name: " + q.getName());
                                continue;
                            }
                            qualities.put(q.getName(), q);
                        }
                        num.getAndIncrement();
                    }
                }
            });
            TerraCraftLogger.success("Loaded " + num + " quality groups");
        } catch (IOException e) {
            TerraCraftLogger.error("Failed to load quality group from " + qualityDir.toAbsolutePath() + " " + e.getMessage());
        }
    }
}
