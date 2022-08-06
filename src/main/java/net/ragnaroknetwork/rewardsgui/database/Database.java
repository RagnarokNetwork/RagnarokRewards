package net.ragnaroknetwork.rewardsgui.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.ragnaroknetwork.rewardsgui.config.Config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class Database {
    private final Logger logger;

    private final HikariDataSource dataSource;
    private final Map<UUID, Map<String, Integer>> cache = new HashMap<>();

    public Database(Config.DatabaseConfig config, Logger logger) {
        this.logger = logger;

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("org.mariadb.jdbc.Driver");
        hikariConfig.setJdbcUrl(String.format("jdbc:mariadb://%s:%s/%s",
                config.host(),
                config.port(),
                config.database()));
        hikariConfig.setUsername(config.user());
        hikariConfig.setPassword(config.password());
        hikariConfig.setMaximumPoolSize(4);
        hikariConfig.setAutoCommit(true);
        hikariConfig.setConnectionTestQuery("SELECT 1");
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");

        dataSource = new HikariDataSource(hikariConfig);
        logger.info("Database Initialized");
    }

    public void loadRewardsDatabase() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS rewards (" +
                    "id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                    "uuid VARCHAR(36) NOT NULL, " +
                    "inventory TEXT NOT NULL DEFAULT '-'" +
                    ");");

            logger.info("Rewards Table initialised");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public PlayerInventory getPlayerInventory(UUID uuid) {
        return new PlayerInventory(this, uuid, cache.get(uuid));
    }

    public void updateCache(UUID uuid, Map<String, Integer> inventory) {
        cache.put(uuid, inventory);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
