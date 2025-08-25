package io.github.tanice.terraCraft.core.util.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.tanice.terraCraft.api.buff.TerraBaseBuff;
import io.github.tanice.terraCraft.api.buff.TerraBuffRecord;
import io.github.tanice.terraCraft.api.player.TerraPlayerData;
import io.github.tanice.terraCraft.api.plugin.TerraPlugin;
import io.github.tanice.terraCraft.api.database.TerraDatabaseManager;
import io.github.tanice.terraCraft.bukkit.util.scheduler.TerraSchedulers;
import io.github.tanice.terraCraft.core.buff.BuffRecord;
import io.github.tanice.terraCraft.core.config.ConfigManager;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import io.github.tanice.terraCraft.core.player.PlayerData;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * 数据库管理
 */
public final class DatabaseManager implements TerraDatabaseManager {
    private final TerraPlugin plugin;
    private Connection connection;

    public DatabaseManager(TerraPlugin plugin) {
        this.plugin = plugin;
        if (!ConfigManager.useMysql()) {
            TerraCraftLogger.info("Data synchronization without database.");
            return;
        }
        this.init();
    }

    public void reload() {
        this.unload();
        this.init();
    }

    public void unload() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                TerraCraftLogger.info("Database connection closed successfully");
            }
        } catch (SQLException e) {
            TerraCraftLogger.error("Failed to close database connection: " + e.getMessage());
        } finally {
            connection = null;
        }
    }

    /**
     * 单独写 -> 考虑到 reload 后更改数据库
     */
    private void init(){
        String url = "jdbc:mysql://" + ConfigManager.getHost() + ":" + ConfigManager.getPort() + "/" + ConfigManager.getDatabase();
        Properties properties = new Properties();
        properties.setProperty("user", ConfigManager.getUsername());
        properties.setProperty("password", ConfigManager.getPassword());
        properties.setProperty("useSSL", "false");
        properties.setProperty("autoReconnect", "true");
        try {
            connection = DriverManager.getConnection(url, properties);
        } catch (SQLException e) {
            TerraCraftLogger.error("Database connection failed! Database synchronization disabled. (If this is your first time running the plugin, please modify database authentication information in the config file)");
            TerraCraftLogger.error(e.getMessage());
            ConfigManager.setUseMysql(false);
            return;
        }
        if (this.createTables()) TerraCraftLogger.success("Database connected successfully");
        else {
            TerraCraftLogger.error("Failed to create database tables, database synchronization disabled");
            ConfigManager.setUseMysql(false);
        }
    }

    // ==================== TerraBuffRecord 相关方法 ====================

    /**
     * 批量保存 TerraBuffRecords 是否为永久 buff 不重要，玩家进入后需要重新加载，会被覆写
     * @param records TerraBuffRecord集合
     */
    @Override
    public void saveBuffRecords(Collection<TerraBuffRecord> records){
        if (records.isEmpty()) return;
        TerraSchedulers.databaseAsync().run(() -> {
            String sql = "INSERT INTO buff_records (uuid, buff_inner_name, cooldown_counter, duration_counter) "
                    + "VALUES (?, ?, ?, ?) "
                    + "ON DUPLICATE KEY UPDATE "
                    + "cooldown_counter = VALUES(cooldown_counter), "
                    + "duration_counter = VALUES(duration_counter)";
            try (PreparedStatement pst = connection.prepareStatement(sql)) {
                for (TerraBuffRecord record : records) {
                    pst.setString(1, record.getEntityReference().toString());
                    pst.setString(2, record.getBuff().getName());
                    pst.setInt(3, record.getCooldownCounter());
                    pst.setInt(4, record.getDurationCounter());
                    pst.addBatch();
                }
                pst.executeBatch();
            } catch (SQLException e) {
                TerraCraftLogger.error("Error saving buff records in batch: " + e.getMessage());
            }
        });
    }

    /**
     * 获取实体的所有TerraBuffRecords并删除数据库的记录
     * @param uuid 实体UUID
     * @return TerraBuffRecord映射表
     */
    @Override
    public CompletableFuture<List<TerraBuffRecord>> loadPlayerBuffRecords(String uuid) {
        CompletableFuture<List<TerraBuffRecord>> future = new CompletableFuture<>();
        TerraSchedulers.databaseAsync().run(() -> {
            List<TerraBuffRecord> res = new ArrayList<>();
            String selectSql = "SELECT buff_inner_name, cooldown_counter, duration_counter FROM buff_records WHERE uuid = ?";
            try (PreparedStatement pst = connection.prepareStatement(selectSql)) {
                pst.setString(1, uuid);
                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        String buffName = rs.getString("buff_name");
                        Optional<TerraBaseBuff> buff = plugin.getBuffManager().getBuff(buffName);
                        if (buff.isEmpty() || !buff.get().enabled()) {
                            TerraCraftLogger.warning("Buff " + buffName + " does not exist or is not enabled");
                            continue;
                        }
                        int cooldown = rs.getInt("cooldown_counter");
                        int duration = rs.getInt("duration_counter");
                        res.add(new BuffRecord(uuid, buff.get(), cooldown, duration));
                    }
                }

                /* 由于写入的时候可能为空，则在读取的时候进行删除 */
                String deleteSql = "DELETE FROM buff_records WHERE uuid = ?";
                try (PreparedStatement deletePst = connection.prepareStatement(deleteSql)) {
                    deletePst.setString(1, uuid);
                    deletePst.executeUpdate();
                }

                future.complete(res);
            } catch (SQLException e) {
                TerraCraftLogger.warning("Failed to load buffs for player: " + Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName() + e.getMessage());
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    // ==================== PlayerData 相关操作 ====================
    /**
     * 保存PlayerData到数据库
     * @param playerData PlayerData对象
     */
    public void savePlayerData(TerraPlayerData playerData) {
        TerraSchedulers.databaseAsync().run(() -> {
            String sql = "INSERT INTO player_data (uuid, health, max_health, mana, max_mana, mana_recovery_speed, ate) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "health = VALUES(health), max_health = VALUES(max_health), " +
                    "mana = VALUES(mana), max_mana = VALUES(max_mana), " +
                    "mana_recovery_speed = VALUES(mana_recovery_speed), ate = VALUES(ate);";

            try (PreparedStatement pst = connection.prepareStatement(sql)) {
                ObjectMapper objectMapper = new ObjectMapper();
                String ateJson = null;

                // 将Map转换为JSON字符串
                if (playerData.getAte() != null) {
                    ateJson = objectMapper.writeValueAsString(playerData.getAte());
                }

                pst.setString(1, playerData.getId().toString());
                pst.setDouble(2, playerData.getHealth());
                pst.setDouble(3, playerData.getMaxHealth());
                pst.setDouble(4, playerData.getMana());
                pst.setDouble(5, playerData.getMaxMana());
                pst.setDouble(6, playerData.getManaRecoverySpeed());
                pst.setString(7, ateJson);  // 设置JSON字符串

                pst.executeUpdate();
            } catch (SQLException | JsonProcessingException e) {
                TerraCraftLogger.error("Failed to save player data: " + e.getMessage());
            }
        });
    }

    // 从数据库加载PlayerData
    public CompletableFuture<TerraPlayerData> loadPlayerData(String uuid) {
        CompletableFuture<TerraPlayerData> future = new CompletableFuture<>();
        TerraSchedulers.databaseAsync().run(() -> {
            String sql = "SELECT health, max_health, mana, max_mana, mana_recovery_speed, ate " +
                    "FROM player_data WHERE uuid = ?";

            try (PreparedStatement pst = connection.prepareStatement(sql)) {
                pst.setString(1, uuid);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        double health = rs.getDouble("health");
                        double maxHealth = rs.getDouble("max_health");
                        double mana = rs.getDouble("mana");
                        double maxMana = rs.getDouble("max_mana");
                        double manaRecoverySpeed = rs.getDouble("mana_recovery_speed");

                        String ateJson = rs.getString("ate");
                        Map<String, Integer> ateMap = null;
                        if (ateJson != null && !ateJson.isEmpty()) {
                            ObjectMapper objectMapper = new ObjectMapper();
                            ateMap = objectMapper.readValue(ateJson, new TypeReference<>() {});
                        }

                        // 构建PlayerData对象（假设构造函数参数顺序对应）
                        TerraPlayerData data = new PlayerData(
                                uuid,
                                health,
                                maxHealth,
                                mana,
                                maxMana,
                                manaRecoverySpeed,
                                ateMap
                        );
                        future.complete(data);
                        return;
                    }
                }
            } catch (SQLException | JsonProcessingException e) {
                TerraCraftLogger.error("Failed to load data for player " + uuid + ": " + e.getMessage());
                future.completeExceptionally(e);
            }
            future.complete(null);
        });
        return future;
    }

    private boolean createTables(){
        String TerraBuffRecordsSql = "CREATE TABLE IF NOT EXISTS buff_records ("
                + "uuid VARCHAR(50) NOT NULL, "
                + "buff_name VARCHAR(50) NOT NULL, "
                + "cooldown_counter INT NOT NULL, "
                + "duration_counter INT NOT NULL, "
                + "PRIMARY KEY (uuid, buff_name))";

        String playerDataTableSql = "CREATE TABLE IF NOT EXISTS player_data ("
                + "uuid VARCHAR(50) PRIMARY KEY, "
                + "health DOUBLE NOT NULL, "
                + "max_health DOUBLE NOT NULL, "
                + "mana DOUBLE NOT NULL, "
                + "max_mana DOUBLE NOT NULL, "
                + "mana_recovery_speed DOUBLE NOT NULL, "
                + "ate TEXT)";

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(TerraBuffRecordsSql);
            stmt.executeUpdate(playerDataTableSql);
        } catch (SQLException e) {
            TerraCraftLogger.error("Failed to create database table: " + e.getMessage());
            return false;
        }
        return true;
    }
}
