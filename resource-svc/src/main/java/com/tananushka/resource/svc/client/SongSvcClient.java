package com.tananushka.resource.svc.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "${svc.song-svc.name}")
public interface SongSvcClient {

    @DeleteMapping("${svc.song-svc.songs-endpoint}/all")
    void deleteAll();

    @GetMapping("${svc.song-svc.health-endpoint}")
    void healthCheck();
}
