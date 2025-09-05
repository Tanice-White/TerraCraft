package io.github.tanice.terraCraft.core.util.database;

import io.github.tanice.terraCraft.api.buff.TerraBaseBuff;
import io.github.tanice.terraCraft.api.buff.TerraBuffRecord;
import io.github.tanice.terraCraft.api.plugin.TerraPlugin;
import io.github.tanice.terraCraft.api.database.TerraDatabaseManager;
import io.github.tanice.terraCraft.bukkit.util.scheduler.TerraSchedulers;
import io.github.tanice.terraCraft.core.buff.BuffRecord;
import io.github.tanice.terraCraft.core.config.ConfigManager;
import io.github.tanice.terraCraft.core.util.logger.TerraCraftLogger;
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
        if (!ConfigManager.useMysql() || records.isEmpty()) return;
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
        if (!ConfigManager.useMysql()) return CompletableFuture.completedFuture(Collections.emptyList());
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

    private boolean createTables(){
        String TerraBuffRecordsSql = "CREATE TABLE IF NOT EXISTS buff_records ("
                + "uuid VARCHAR(50) NOT NULL, "
                + "buff_name VARCHAR(50) NOT NULL, "
                + "cooldown_counter INT NOT NULL, "
                + "duration_counter INT NOT NULL, "
                + "PRIMARY KEY (uuid, buff_name))";

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(TerraBuffRecordsSql);
        } catch (SQLException e) {
            TerraCraftLogger.error("Failed to create database table: " + e.getMessage());
            return false;
        }
        return true;
    }
}
