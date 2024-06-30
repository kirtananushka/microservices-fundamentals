package com.tananushka.resource.proc.config.health;

import com.tananushka.resource.proc.client.SongSvcClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SongSvcHealthIndicator implements HealthIndicator {

    private final SongSvcClient songSvcClient;

    @Override
    public Health health() {
        try {
            songSvcClient.healthCheck();
            return Health.up().withDetail("song-service", "Available").build();
        } catch (Exception e) {
            return Health.down(e).withDetail("song-service", "Unavailable").build();
        }
    }
}
