package com.tananushka.resource.svc.client;

import com.tananushka.resource.svc.config.logging.FeignClientLoggingConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "${svc.song-svc.name}", configuration = FeignClientLoggingConfig.class)
public interface SongSvcClient {

    @DeleteMapping("${svc.song-svc.songs-endpoint}/all")
    void deleteAll();

    @DeleteMapping("${svc.song-svc.songs-endpoint}")
    void deleteSongsById(@RequestParam String id);
    
    @GetMapping("${svc.song-svc.health-endpoint}")
    void healthCheck();
}
