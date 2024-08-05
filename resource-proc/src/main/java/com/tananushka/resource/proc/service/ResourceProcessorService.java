package com.tananushka.resource.proc.service;

import com.tananushka.resource.proc.client.ResourceSvcClient;
import com.tananushka.resource.proc.client.SongSvcClient;
import com.tananushka.resource.proc.dto.MetadataRequest;
import com.tananushka.resource.proc.exception.ResourceProcessorException;
import jakarta.jms.Message;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.metadata.Metadata;
import org.slf4j.MDC;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

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
  @SneakyThrows
  public void processResource(Message message) {
    Long resourceId = message.getBody(Long.class);
    String traceId = message.getStringProperty("X-Trace-Id");
    if (traceId != null) {
      MDC.put("traceId", traceId);
    }
    log.debug("Received a new message with resourceId={}", resourceId);
    try {
      TimeUnit.SECONDS.sleep(2);
      byte[] audioData = resourceSvcClient.getAudioData(Math.toIntExact(resourceId)).getBody();
      Metadata metadata = mp3MetadataService.extractMetadata(audioData);
      MetadataRequest metadataRequest = mp3MetadataService.createSongRequest(metadata);
      metadataRequest.setId(Math.toIntExact(resourceId));
      retryTemplate.execute(context -> {
        log.debug("Attempt #{} to send resourceId={} metadata to the Song Service", context.getRetryCount() + 1, resourceId);
        songSvcClient.saveMetadata(metadataRequest);
        return null;
      });
      log.debug("Sent metadata for resourceId={} to the Song Service", resourceId);
    } catch (InterruptedException e) {
      log.error("Interrupted while waiting before processing resourceId={}", resourceId, e);
      Thread.currentThread().interrupt();
    } catch (Exception e) {
      log.error("Error occurred while processing resourceId={}: {}", resourceId, e.getMessage());
      throw new ResourceProcessorException("Failed to process resourceId=" + resourceId, "500");
    } finally {
      MDC.clear();
    }
  }
}
