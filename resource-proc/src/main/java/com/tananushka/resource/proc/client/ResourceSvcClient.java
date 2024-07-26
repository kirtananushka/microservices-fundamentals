package com.tananushka.resource.proc.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "${svc.resource-svc.name}", fallback = ResourceSvcClient.ResourceClientFallback.class)
public interface ResourceSvcClient {
    @GetMapping(value = "${svc.resource-svc.resources-endpoint}/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    ResponseEntity<byte[]> getAudioData(@PathVariable Integer id);


    @GetMapping("${svc.resource-svc.health-endpoint}")
    void healthCheck();

    @Component("resourceSvcClient")
    @Slf4j
    class ResourceClientFallback implements ResourceSvcClient {
        @Override
        public ResponseEntity<byte[]> getAudioData(Integer id) {
            log.warn("Fallback called: Unable to get audio data for resourceId={}", id);
            return ResponseEntity.ok().body(new byte[0]);
        }

        @Override
        public void healthCheck() {
            log.warn("Fallback called: Unable to check health of resource service");
        }
    }
}
