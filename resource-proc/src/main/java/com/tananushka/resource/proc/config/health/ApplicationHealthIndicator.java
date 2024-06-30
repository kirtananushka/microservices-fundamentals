package com.tananushka.resource.proc.config.health;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class ApplicationHealthIndicator implements HealthIndicator {

    private final Environment environment;

    @Value("${app.version:unknown}")
    private String appVersion;

    @Override
    public Health health() {
        return Health.up().withDetail("applicationHealthIndicator", "Healthy")
                .withDetail("activeProfiles", getCurrentProfiles())
                .withDetail("version", appVersion)
                .withDetail("uptime", getUptime())
                .withDetail("memory", getMemoryDetails())
                .withDetail("activeThreads", Thread.activeCount())
                .withDetail("diskUsage", getDiskUsage())
                .build();
    }

    private String getCurrentProfiles() {
        String[] activeProfiles = environment.getActiveProfiles();
        List<String> profiles = Arrays.stream(activeProfiles).map(String::toUpperCase).toList();
        if (profiles.isEmpty()) {
            return "DEFAULT";
        }
        return String.join(", ", profiles);
    }

    private String getUptime() {
        long uptimeMillis = ManagementFactory.getRuntimeMXBean().getUptime();
        long days = TimeUnit.MILLISECONDS.toDays(uptimeMillis);
        long hours = TimeUnit.MILLISECONDS.toHours(uptimeMillis) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(uptimeMillis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(uptimeMillis) % 60;

        StringBuilder uptimeBuilder = new StringBuilder();
        if (days > 0) {
            uptimeBuilder.append(days).append(" days");
        }
        if (hours > 0) {
            if (!uptimeBuilder.isEmpty())
                uptimeBuilder.append(", ");
            uptimeBuilder.append(hours).append(" h");
        }
        if (minutes > 0) {
            if (!uptimeBuilder.isEmpty())
                uptimeBuilder.append(", ");
            uptimeBuilder.append(minutes).append(" min");
        }
        if (seconds > 0 || uptimeBuilder.isEmpty()) {
            if (!uptimeBuilder.isEmpty())
                uptimeBuilder.append(", ");
            uptimeBuilder.append(seconds).append(" sec");
        }

        return uptimeBuilder.toString();
    }

    private Map<String, Object> getMemoryDetails() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        String heapUsageMB = bytesToMegabytes(memoryMXBean.getHeapMemoryUsage().getUsed());
        String maxHeapUsageMB = bytesToMegabytes(memoryMXBean.getHeapMemoryUsage().getMax());
        String nonHeapUsageMB = bytesToMegabytes(memoryMXBean.getNonHeapMemoryUsage().getUsed());
        String maxNonHeapUsageMB = bytesToMegabytes(memoryMXBean.getNonHeapMemoryUsage().getMax());

        Map<String, Object> memoryDetails = new HashMap<>();
        memoryDetails.put("heapUsageMB", heapUsageMB);
        memoryDetails.put("maxHeapUsageMB", maxHeapUsageMB);
        memoryDetails.put("nonHeapUsageMB", nonHeapUsageMB);
        memoryDetails.put("maxNonHeapUsageMB", maxNonHeapUsageMB);

        return memoryDetails;
    }

    private Map<String, Object> getDiskUsage() {
        File file = new File(".");
        String totalSpaceMB = bytesToMegabytes(file.getTotalSpace());
        String usableSpaceMB = bytesToMegabytes(file.getUsableSpace());
        String freeSpaceMB = bytesToMegabytes(file.getFreeSpace());

        Map<String, Object> diskUsage = new HashMap<>();
        diskUsage.put("totalSpaceMB", totalSpaceMB);
        diskUsage.put("usableSpaceMB", usableSpaceMB);
        diskUsage.put("freeSpaceMB", freeSpaceMB);

        return diskUsage;
    }

    private String bytesToMegabytes(long bytes) {
        return String.format("%.2f", bytes / (1024.0 * 1024));
    }
}
