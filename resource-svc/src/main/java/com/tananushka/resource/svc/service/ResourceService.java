package com.tananushka.resource.svc.service;

import com.tananushka.resource.svc.client.SongClient;
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

@AllArgsConstructor
@Service
@Slf4j
public class ResourceService {

    private final ResourceMapper resourceMapper;

    private final ResourceRepository resourceRepository;

    private final SongClient songClient;

    @Transactional
    public ResourceResponse saveResource(byte[] audioData) {
        Metadata metadata = getMetadata(audioData);
        Resource savedResource = createResource(audioData);

        SongRequest songRequest = resourceMapper.toRequest(savedResource.getId(), metadata);
        log.debug("SongRequest: {}", songRequest);

        SongIdResponse songIdResponse = songClient.saveMetadata(songRequest);
        log.debug("SongIdResponse: {}", songIdResponse);

        return resourceMapper.toResponse(savedResource);
    }

    public byte[] getResourceData(Integer id) {
        validateResourceExistence(id);
        return resourceRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new ResourceServiceException("Unexpected error", "500")).getAudioData();
    }

    public List<ResourceResponse> findAllIds() {
        return resourceRepository.findAllIds()
                .stream()
                .map(resourceMapper::toResponse).toList();
    }

    @Transactional
    public List<Long> deleteResources(String csvIds) {
        validateCsvIdsString(csvIds);
        List<Long> ids = parseCsvIds(csvIds);
        List<Long> existingIds = validateResourceExistence(ids);
        resourceRepository.deleteByIdIn(existingIds);
        return existingIds;
    }

    @Transactional
    public void deleteAll() {
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

    private Resource createResource(byte[] audioData) {
        Resource resource = new Resource();
        resource.setAudioData(audioData);
        return resourceRepository.save(resource);
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

