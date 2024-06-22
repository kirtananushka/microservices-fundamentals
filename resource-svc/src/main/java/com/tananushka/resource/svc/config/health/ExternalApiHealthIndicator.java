package com.tananushka.resource.svc.config.health;

import com.tananushka.resource.svc.client.SongClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExternalApiHealthIndicator implements HealthIndicator {

    private final SongClient songClient;

    @Override
    public Health health() {
        try {
            songClient.healthCheck();
            return Health.up().withDetail("song-service", "Available").build();
        } catch (Exception e) {
            return Health.down(e).withDetail("song-service", "Unavailable").build();
        }
    }
}
