package com.tananushka.resource.proc.config.health;

import com.tananushka.resource.proc.client.ResourceSvcClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResourceSvcHealthIndicator implements HealthIndicator {

    private final ResourceSvcClient resourceSvcClient;

    @Override
    public Health health() {
        try {
            resourceSvcClient.healthCheck();
            return Health.up().withDetail("resource-service", "Available").build();
        } catch (Exception e) {
            return Health.down(e).withDetail("resource-service", "Unavailable").build();
        }
    }
}
