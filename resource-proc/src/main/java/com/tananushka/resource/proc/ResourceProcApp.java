package com.tananushka.resource.proc;

import org.apache.tika.parser.mp3.Mp3Parser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class ResourceProcApp {
    public static void main(String[] args) {
        SpringApplication.run(ResourceProcApp.class, args);
    }

    @Bean
    public Mp3Parser mp3Parser() {
        return new Mp3Parser();
    }
}
