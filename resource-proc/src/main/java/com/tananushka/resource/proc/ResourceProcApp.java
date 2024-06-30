package com.tananushka.resource.proc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class ResourceProcApp {
    public static void main(String[] args) {
        SpringApplication.run(ResourceProcApp.class, args);
    }
}
