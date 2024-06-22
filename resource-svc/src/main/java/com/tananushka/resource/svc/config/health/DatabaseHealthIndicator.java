package com.tananushka.resource.svc.config.health;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DatabaseHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;

    private final HikariDataSource hikariDataSource;

    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String databaseProductName = metaData.getDatabaseProductName();
            String databaseProductVersion = metaData.getDatabaseProductVersion();
            String url = metaData.getURL();
            String userName = metaData.getUserName();

            if (connection.isValid(1)) {
                return Health.up().withDetail("Database", "Connected").withDetail("Database Type", databaseProductName)
                        .withDetail("Database Version", databaseProductVersion).withDetail("URL", url)
                        .withDetail("Username", userName).withDetail("dbPool", getDbPoolStats()).build();
            } else {
                return Health.down().withDetail("Database", "Not Connected").build();
            }
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }

    private Map<String, Object> getDbPoolStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("activeConnections", hikariDataSource.getHikariPoolMXBean().getActiveConnections());
        stats.put("idleConnections", hikariDataSource.getHikariPoolMXBean().getIdleConnections());
        stats.put("totalConnections", hikariDataSource.getHikariPoolMXBean().getTotalConnections());
        stats.put("threadsAwaitingConnection", hikariDataSource.getHikariPoolMXBean().getThreadsAwaitingConnection());
        return stats;
    }
}
