package com.tananushka.resource.svc.client;

import com.tananushka.resource.svc.dto.SongIdResponse;
import com.tananushka.resource.svc.dto.SongRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "${svc.song-svc.name}")
public interface SongClient {

    @GetMapping("${svc.song-svc.songs-endpoint}")
    SongIdResponse saveMetadata(@RequestBody SongRequest songRequest);

    @DeleteMapping("${svc.song-svc.songs-endpoint}/all")
    void deleteAll();

    @GetMapping("${svc.song-svc.health-endpoint}")
    void healthCheck();
}
