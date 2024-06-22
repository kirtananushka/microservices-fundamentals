package com.tananushka.song.svc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableDiscoveryClient
public class SongSvcApplication {

    public static void main(String[] args) {
        SpringApplication.run(SongSvcApplication.class, args);
    }

}
