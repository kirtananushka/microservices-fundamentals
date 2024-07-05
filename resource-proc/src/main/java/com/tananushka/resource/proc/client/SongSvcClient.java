package com.tananushka.resource.proc.client;

import com.tananushka.resource.proc.dto.MetadataRequest;
import com.tananushka.resource.proc.dto.SongIdResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "${svc.song-svc.name}",
        fallback = SongSvcClient.SongClientFallback.class
)
public interface SongSvcClient {
    @PostMapping("${svc.song-svc.songs-endpoint}")
    SongIdResponse saveMetadata(@RequestBody MetadataRequest metadataRequest);

    @GetMapping("${svc.song-svc.health-endpoint}")
    void healthCheck();

    @Component("songSvcClient")
    @Slf4j
    class SongClientFallback implements SongSvcClient {
        @Override
        public SongIdResponse saveMetadata(MetadataRequest metadataRequest) {
            log.warn("Fallback called: Unable to save metadata for resourceId={}", metadataRequest.getId());
            return new SongIdResponse(-1);
        }

        @Override
        public void healthCheck() {
            log.warn("Fallback called: Unable to check health of song service");
        }
    }
}
