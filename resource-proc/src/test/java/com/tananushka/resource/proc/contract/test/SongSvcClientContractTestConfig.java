package com.tananushka.resource.proc.contract.test;

import com.tananushka.resource.proc.client.SongSvcClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClientBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@TestConfiguration
@Profile("contract-test")
@EnableFeignClients
public class SongSvcClientContractTestConfig {

    @Bean
    public SongSvcClient testSongSvcClient(ApplicationContext context,
                                           @Value("${svc.song-svc.url}") String url,
                                           @Value("${svc.song-svc.name}") String name) {
        return new FeignClientBuilder(context)
                .forType(SongSvcClient.class, name)
                .url(url)
                .build();
    }
}