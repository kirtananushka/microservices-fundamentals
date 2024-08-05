package com.tananushka.resource.svc.service;

import com.tananushka.resource.svc.client.SongSvcClient;
import com.tananushka.resource.svc.client.StorageSvcClient;
import com.tananushka.resource.svc.dto.ResourceResponse;
import com.tananushka.resource.svc.dto.Storage;
import com.tananushka.resource.svc.entity.Resource;
import com.tananushka.resource.svc.exception.ResourceServiceException;
import com.tananushka.resource.svc.interceptor.FeignTraceIdInterceptor;
import com.tananushka.resource.svc.mapper.ResourceMapper;
import com.tananushka.resource.svc.repository.ResourceRepository;
import jakarta.jms.JMSException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
@Slf4j
public class ResourceService {
    private static final String PERMANENT = "PERMANENT";
    private static final String STAGING = "STAGING";
    private final ResourceMapper resourceMapper;
    private final ResourceRepository resourceRepository;
    private final SongSvcClient songSvcClient;
    private final S3Service s3Service;
    private final JmsTemplate jmsTemplate;
    private final StorageSvcClient storageSvcClient;

    @Value("${queue.resource}")
    private String resourceQueue;

    private final AtomicInteger getResourceDataTestCounter = new AtomicInteger(0);

    @Transactional
    public ResourceResponse saveResource(byte[] audioData) {
        log.debug("Starting saveResource with audio data of length: {}", audioData.length);
        validateAudioData(audioData);

        // Fetch the storage type from the service
        Storage stagingStorage = getStorageByType(STAGING);
        String stagingS3Location = s3Service.uploadToS3(audioData, stagingStorage);

        // Create resource record with staging storage type
        Resource savedResource = createResourceRecord(stagingS3Location, STAGING);
        resourceRepository.save(savedResource);

        log.info("Resource saved with ID: {} and {} S3 location: {}", savedResource.getId(), STAGING, stagingS3Location);

        Long resourceId = savedResource.getId();
        sendMessage(resourceId);

        // Move to permanent storage
        String permanentS3Location = moveToPermanentStorage(resourceId);
        savedResource.setS3Location(permanentS3Location);
        savedResource.setStorageType(PERMANENT);
        resourceRepository.save(savedResource);

        log.info("Resource updated with {} S3 location: {}", PERMANENT, permanentS3Location);

        return resourceMapper.toResponse(savedResource);
    }

    @Retryable(retryFor = {Exception.class}, maxAttemptsExpression = "#{${retry.maxAttempts}}", backoff = @Backoff(delayExpression = "#{${retry.delay}}", multiplierExpression = "#{${retry.multiplier}}"))
    public void sendMessage(Long resourceId) {
        jmsTemplate.convertAndSend(resourceQueue, resourceId, message -> {
            log.info("Sending message with resourceId={} to the queue={}", resourceId, resourceQueue);
            String traceId = MDC.get("traceId");
            if (traceId != null) {
                message.setStringProperty(FeignTraceIdInterceptor.TRACE_ID_HEADER, traceId);
            }
            try {
                message.acknowledge();
                log.info("Message with resourceId={} acknowledged", resourceId);
            } catch (JMSException e) {
                log.error("Failed to acknowledge message with resourceId={}", resourceId);
                throw new ResourceServiceException("Failed to acknowledge message", "500");
            }
            return message;
        });
    }

    @Retryable(retryFor = {Exception.class}, maxAttemptsExpression = "#{${retry.maxAttempts}}", backoff = @Backoff(delayExpression = "#{${retry.delay}}", multiplierExpression = "#{${retry.multiplier}}"))
    public byte[] getResourceData(Integer id) {
        Resource resource = resourceRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new ResourceServiceException("File with ID=" + id + " is not ready yet and cannot be retrieved from s3", "5001"));
        return s3Service.downloadFromS3(resource.getS3Location());
    }

    public byte[] getResourceDataWithValidation(Integer id) {
        validateResourceExistence(id);
        return getResourceData(id);
    }

    public List<ResourceResponse> findAll() {
        return resourceRepository.findAllIds().stream().map(resourceId -> {
            Resource resource = resourceRepository.findById(resourceId)
                    .orElseThrow(() -> new ResourceServiceException("Unexpected error", "500"));
            return resourceMapper.toResponse(resource);
        }).toList();
    }

    @Transactional
    public List<Long> deleteResources(String csvIds) {
        validateCsvIdsString(csvIds);
        List<Long> ids = parseCsvIds(csvIds);
        List<Long> existingIds = validateResourceExistence(ids);
        List<Resource> resourcesToDelete = resourceRepository.findAllById(existingIds);
        resourcesToDelete.forEach(resource -> {
            try {
                s3Service.deleteFromS3(resource.getS3Location(), resource.getStorageType());
            } catch (Exception e) {
                log.error("Failed to delete file from S3 for resource ID: {}", resource.getId(), e);
            }
        });
        resourceRepository.deleteByIdIn(existingIds);
        String existingIdsStr = String.join(",", existingIds.stream().map(String::valueOf).toList());
        songSvcClient.deleteSongsById(existingIdsStr);
        return existingIds;
    }

    @Transactional
    public void deleteAll() {
        List<Resource> allResources = resourceRepository.findAll();
        allResources.forEach(resource -> {
            try {
                s3Service.deleteFromS3(resource.getS3Location(), resource.getStorageType());
            } catch (Exception e) {
                log.error("Failed to delete file from S3 for resource ID: {}", resource.getId(), e);
            }
        });
        songSvcClient.deleteAll();
        resourceRepository.deleteAll();
    }

    private void validateAudioData(byte[] audioData) {
        String mimeType = new Tika().detect(audioData);
        if (!mimeType.equals("audio/mpeg")) {
            throw new ResourceServiceException("Invalid audio data: " + mimeType, "400");
        }
    }

    private void validateCsvIdsString(String csvIds) {
        if (csvIds.length() >= 200) {
            throw new ResourceServiceException("Invalid CSV length", "400");
        }
    }

    private List<Long> parseCsvIds(String csvIds) {
        return Stream.of(csvIds.split(",")).map(Long::parseLong).toList();
    }

    private void validateResourceExistence(Integer id) {
        if (!resourceRepository.existsById(Long.valueOf(id))) {
            throw new ResourceServiceException(String.format("Resource with ID=%d not found", id), "404");
        }
    }

    private List<Long> validateResourceExistence(List<Long> ids) {
        return resourceRepository.findAllById(ids).stream().map(Resource::getId).toList();
    }

    private Resource createResourceRecord(String s3Location, String storageType) {
        Resource resource = new Resource();
        resource.setS3Location(s3Location);
        resource.setStorageType(storageType);
        return resourceRepository.save(resource);
    }

    public Storage getStorageByType(String storageType) {
        String traceId = MDC.get("traceId");
        return storageSvcClient.getStorages(traceId).stream()
                .filter(storage -> storage.getStorageType().equalsIgnoreCase(storageType))
                .findFirst()
                .orElseThrow(() -> new ResourceServiceException("Storage type not found: " + storageType, "500"));
    }

    private String moveToPermanentStorage(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceServiceException("Resource not found", "404"));
        String newS3Location = moveToPermanent(resource.getS3Location(), resource.getStorageType());
        resource.setS3Location(newS3Location);
        resourceRepository.save(resource);
        return newS3Location;
    }

    private String moveToPermanent(String s3Location, String currentStorageType) {
        byte[] data = s3Service.downloadFromS3(s3Location);
        s3Service.deleteFromS3(s3Location, currentStorageType);
        Storage permanentStorage = getStorageByType(PERMANENT);
        return s3Service.uploadToS3(data, permanentStorage);
    }

    @Retryable(retryFor = {Exception.class}, maxAttemptsExpression = "#{${retry.maxAttempts}}", backoff = @Backoff(delayExpression = "#{${retry.delay}}", multiplierExpression = "#{${retry.multiplier}}"))
    public Boolean getResourceDataWithExceptionToTestRetry(Integer id) {
        if (getResourceDataTestCounter.incrementAndGet() <= 4) { // Fail twice intentionally
            log.info("Intentionally failing getResourceData, attempt {}", getResourceDataTestCounter.get());
            throw new RuntimeException("Intentional failure for testing retries in getResourceData");
        }
        log.info("getResourceDataWithExceptionToTest successful");
        return true;
    }
}
