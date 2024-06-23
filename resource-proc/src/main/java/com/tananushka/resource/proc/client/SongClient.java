package com.tananushka.resource.proc.client;

import com.tananushka.resource.proc.dto.SongIdResponse;
import com.tananushka.resource.proc.dto.SongRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "${svc.song-svc.name}")
public interface SongClient {

    @PostMapping("${svc.song-svc.songs-endpoint}")
    SongIdResponse saveMetadata(@RequestBody SongRequest songRequest);
}
