package com.tananushka.resource.svc.service;

import com.tananushka.resource.svc.client.SongClient;
import com.tananushka.resource.svc.config.aws.AwsProperties;
import com.tananushka.resource.svc.dto.ResourceResponse;
import com.tananushka.resource.svc.dto.SongIdResponse;
import com.tananushka.resource.svc.dto.SongRequest;
import com.tananushka.resource.svc.entity.Resource;
import com.tananushka.resource.svc.exception.ResourceServiceException;
import com.tananushka.resource.svc.mapper.ResourceMapper;
import com.tananushka.resource.svc.repository.ResourceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@AllArgsConstructor
@Service
@Slf4j
public class ResourceService {

    private final ResourceMapper resourceMapper;
    private final ResourceRepository resourceRepository;
    private final SongClient songClient;
    private final S3Client s3Client;
    private final AwsProperties awsProperties;

    @Transactional
    public ResourceResponse saveResource(byte[] audioData) {
        Metadata metadata = getMetadata(audioData);
        Resource savedResource = createResourceRecord(metadata);
        String s3Location;

        try {
            s3Location = uploadToS3(audioData);
            savedResource.setS3Location(s3Location);
            resourceRepository.save(savedResource);
        } catch (Exception e) {
            throw new ResourceServiceException("Failed to upload file to S3", "500", e);
        }

        return resourceMapper.toResponse(savedResource.getId(), s3Location);
    }

    private Resource createResourceRecord(Metadata metadata) {
        Resource resource = new Resource();
        resource.setS3Location("");
        Resource savedResource = resourceRepository.save(resource);

        SongRequest songRequest = resourceMapper.toRequest(savedResource.getId(), metadata);
        log.debug("SongRequest: {}", songRequest);
        try {
            SongIdResponse songIdResponse = songClient.saveMetadata(songRequest);
            log.debug("SongIdResponse: {}", songIdResponse);
        } catch (Exception e) {
            throw new ResourceServiceException("Failed to save song metadata", "500", e);
        }

        return savedResource;
    }

    public byte[] getResourceData(Integer id) {
        validateResourceExistence(id);
        String s3Location = resourceRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new ResourceServiceException("Unexpected error", "500")).getS3Location();
        return downloadFromS3(s3Location);
    }

    public List<ResourceResponse> findAll() {
        return resourceRepository.findAllIds()
                .stream()
                .map(resourceId -> {
                    String s3Location = resourceRepository.findById(resourceId)
                            .orElseThrow(() -> new ResourceServiceException("Unexpected error", "500"))
                            .getS3Location();
                    return resourceMapper.toResponse(resourceId, s3Location);
                }).toList();
    }

//    @Transactional
//    public List<Long> deleteResources(String csvIds) {
//        validateCsvIdsString(csvIds);
//        List<Long> ids = parseCsvIds(csvIds);
//        List<Long> existingIds = validateResourceExistence(ids);
//        resourceRepository.deleteByIdIn(existingIds);
//        return existingIds;
//    }

    @Transactional
    public List<Long> deleteResources(String csvIds) {
        validateCsvIdsString(csvIds);
        List<Long> ids = parseCsvIds(csvIds);
        List<Long> existingIds = validateResourceExistence(ids);

        List<Resource> resourcesToDelete = resourceRepository.findAllById(existingIds);
        resourcesToDelete.forEach(resource -> {
            try {
                deleteFromS3(resource.getS3Location());
            } catch (Exception e) {
                log.error("Failed to delete file from S3 for resource ID: {}", resource.getId(), e);
            }
        });

        resourceRepository.deleteByIdIn(existingIds);
        return existingIds;
    }

    private void deleteFromS3(String s3Location) {
        String bucketName = awsProperties.getS3().getBucket();
        String key = s3Location.substring(s3Location.lastIndexOf("/") + 1);
        try {
            s3Client.deleteObject(builder -> builder.bucket(bucketName).key(key));
        } catch (Exception e) {
            throw new ResourceServiceException("Failed to delete file from S3: " + s3Location, "500", e);
        }
    }

    @Transactional
    public void deleteAll() {
        List<Resource> allResources = resourceRepository.findAll();
        allResources.forEach(resource -> {
            try {
                deleteFromS3(resource.getS3Location());
            } catch (Exception e) {
                log.error("Failed to delete file from S3 for resource ID: {}", resource.getId(), e);
            }
        });
        songClient.deleteAll();
        resourceRepository.deleteAll();
    }

    private Metadata getMetadata(byte[] audioData) {
        validateAudioData(audioData);
        return extractMp3Metadata(audioData);
    }

    private void validateAudioData(byte[] audioData) {
        String mimeType = new Tika().detect(audioData);
        if (!mimeType.equals("audio/mpeg")) {
            throw new ResourceServiceException("Invalid audio data: " + mimeType, "400");
        }
    }

    private Metadata extractMp3Metadata(byte[] audioData) {
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        try (InputStream input = new ByteArrayInputStream(audioData)) {
            new Mp3Parser().parse(input, handler, metadata, new ParseContext());
            return metadata;
        } catch (IOException | TikaException | SAXException e) {
            throw new ResourceServiceException("Failed to extract metadata", "400", e);
        }
    }

    private String uploadToS3(byte[] audioData) {
        String key = UUID.randomUUID().toString() + ".mp3";
        try {
            File file = File.createTempFile(key, null);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(audioData);
            }
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(awsProperties.getS3().getBucket())
                    .key(key)
                    .build();
            PutObjectResponse putObjectResponse = s3Client.putObject(putObjectRequest,
                    software.amazon.awssdk.core.sync.RequestBody.fromFile(file));
            return "s3://" + awsProperties.getS3().getBucket() + "/" + key;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }

    private byte[] downloadFromS3(String s3Location) {
        String bucketName = awsProperties.getS3().getBucket();
        String key = s3Location.substring(s3Location.lastIndexOf("/") + 1);
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        return s3Client.getObject(getObjectRequest, ResponseTransformer.toBytes()).asByteArray();
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
}
