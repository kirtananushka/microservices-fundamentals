package com.tananushka.resource.proc.service;

import com.tananushka.resource.proc.client.ResourceSvcClient;
import com.tananushka.resource.proc.client.SongSvcClient;
import com.tananushka.resource.proc.dto.MetadataRequest;
import com.tananushka.resource.proc.exception.ResourceProcessorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.metadata.Metadata;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResourceProcessorService {
    private final SongSvcClient songSvcClient;
    private final ResourceSvcClient resourceSvcClient;
    private final Mp3MetadataService mp3MetadataService;
    private final RetryTemplate retryTemplate;

    @Retryable(
            retryFor = {Exception.class},
            maxAttemptsExpression = "#{${retry.maxAttempts}}",
            backoff = @Backoff(
                    delayExpression = "#{${retry.delay}}",
                    multiplierExpression = "#{${retry.multiplier}}"
            )
    )
    @JmsListener(destination = "${queue.resource}")
    public void processResource(Long resourceId) {
        log.debug("Received new resourceId={}", resourceId);
        try {
            byte[] audioData = resourceSvcClient.getAudioData(Math.toIntExact(resourceId));
            Metadata metadata = mp3MetadataService.extractMetadata(audioData);
            MetadataRequest metadataRequest = mp3MetadataService.createSongRequest(metadata);
            metadataRequest.setId(Math.toIntExact(resourceId));
            retryTemplate.execute(context -> {
                log.debug("Attempt #{} to save metadata for resourceId={}", context.getRetryCount() + 1, resourceId);
                songSvcClient.saveMetadata(metadataRequest);
                return null;
            });
            log.debug("Saved metadata for resourceId={} to the Song Service", resourceId);
        } catch (Exception e) {
            log.error("Error occurred while processing resourceId={}: {}", resourceId, e.getMessage());
            throw new ResourceProcessorException("Failed to process resourceId=" + resourceId, "500");
        }
    }
}
