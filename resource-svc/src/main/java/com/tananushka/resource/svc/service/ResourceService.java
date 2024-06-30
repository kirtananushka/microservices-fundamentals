package com.tananushka.resource.svc.service;

import com.tananushka.resource.svc.client.SongSvcClient;
import com.tananushka.resource.svc.dto.ResourceResponse;
import com.tananushka.resource.svc.entity.Resource;
import com.tananushka.resource.svc.exception.ResourceServiceException;
import com.tananushka.resource.svc.mapper.ResourceMapper;
import com.tananushka.resource.svc.repository.ResourceRepository;
import jakarta.jms.JMSException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
@Slf4j
public class ResourceService {
    private final ResourceMapper resourceMapper;
    private final ResourceRepository resourceRepository;
    private final SongSvcClient songSvcClient;
    private final S3Service s3Service;
    private final JmsTemplate jmsTemplate;

    @Value("${queue.resource}")
    private String resourceQueue;

    @Transactional
    public ResourceResponse saveResource(byte[] audioData) {
        validateAudioData(audioData);
        Resource savedResource = createResourceRecord();
        String s3Location;
        try {
            s3Location = s3Service.uploadToS3(audioData);
            savedResource.setS3Location(s3Location);
            resourceRepository.save(savedResource);
        } catch (Exception e) {
            throw new ResourceServiceException("Failed to upload file to S3", "500", e);
        }
        sendMessage(savedResource.getId());
        return resourceMapper.toResponse(savedResource.getId(), s3Location);
    }

    @Retryable(retryFor = {Exception.class}, maxAttemptsExpression = "#{${retry.maxAttempts}}", backoff = @Backoff(delayExpression = "#{${retry.delay}}", multiplierExpression = "#{${retry.multiplier}}"))
    public void sendMessage(Long resourceId) {
        jmsTemplate.convertAndSend(resourceQueue, resourceId, message -> {
            log.debug("Message sent with resourceId={} to the queue={}", resourceId, resourceQueue);
            try {
                message.acknowledge();
                log.debug("Message with resourceId={} acknowledged", resourceId);
            } catch (JMSException e) {
                log.error("Failed to acknowledge message with resourceId={}", resourceId);
                throw new ResourceServiceException("Failed to acknowledge message", "500");
            }
            return message;
        });
    }

    public byte[] getResourceData(Integer id) {
        String s3Location = resourceRepository.findById(Long.valueOf(id)).orElseThrow(() -> new ResourceServiceException("Unexpected error", "500")).getS3Location();
        return s3Service.downloadFromS3(s3Location);
    }

    public byte[] getResourceDataWithValidation(Integer id) {
        validateResourceExistence(id);
        return getResourceData(id);
    }

    public List<ResourceResponse> findAll() {
        return resourceRepository.findAllIds().stream().map(resourceId -> {
            String s3Location = resourceRepository.findById(resourceId).orElseThrow(() -> new ResourceServiceException("Unexpected error", "500")).getS3Location();
            return resourceMapper.toResponse(resourceId, s3Location);
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
                s3Service.deleteFromS3(resource.getS3Location());
            } catch (Exception e) {
                log.error("Failed to delete file from S3 for resource ID: {}", resource.getId(), e);
            }
        });
        resourceRepository.deleteByIdIn(existingIds);
        return existingIds;
    }

    @Transactional
    public void deleteAll() {
        List<Resource> allResources = resourceRepository.findAll();
        allResources.forEach(resource -> {
            try {
                s3Service.deleteFromS3(resource.getS3Location());
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

    private Resource createResourceRecord() {
        Resource resource = new Resource();
        resource.setS3Location("");
        return resourceRepository.save(resource);
    }
}
