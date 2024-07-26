package com.tananushka.resource.svc.client;

import com.tananushka.resource.svc.dto.Storage;
import com.tananushka.resource.svc.util.StorageFallbackUtil;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "${svc.storage-svc.name}", fallback = StorageSvcClient.StorageSvcClientFallback.class)
public interface StorageSvcClient {

    @GetMapping("${svc.storage-svc.storages-endpoint}")
    @CircuitBreaker(name = "storageSvcClient")
    List<Storage> getStorages();

    @GetMapping("${svc.storage-svc.health-endpoint}")
    void healthCheck();

    @Component
    class StorageSvcClientFallback implements StorageSvcClient {
        @Override
        public List<Storage> getStorages() {
            return StorageFallbackUtil.getDefaultStorages();
        }

        @Override
        public void healthCheck() {
            // Do nothing
        }
    }
}
