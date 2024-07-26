package com.tananushka.storage.svc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableDiscoveryClient
public class StorageSvcApp {

    public static void main(String[] args) {
        SpringApplication.run(StorageSvcApp.class, args);
    }
}
