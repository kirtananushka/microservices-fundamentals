package com.tananushka.resource.svc.mapper;

import com.tananushka.resource.svc.dto.ResourceResponse;
import com.tananushka.resource.svc.dto.SongRequest;
import com.tananushka.resource.svc.entity.Resource;
import org.apache.tika.metadata.Metadata;
import org.springframework.stereotype.Component;

@Component
public class ResourceMapper {
    public ResourceResponse toResponse(Resource resource) {
        ResourceResponse response = new ResourceResponse();
        response.setId(Math.toIntExact(resource.getId()));
        response.setS3Url(resource.getS3Location());
        return response;
    }

    public ResourceResponse toResponse(Long resourceId, String s3Url) {
        ResourceResponse response = new ResourceResponse();
        response.setId(Math.toIntExact(resourceId));
        response.setS3Url(s3Url);
        return response;
    }

    public SongRequest toRequest(Long id, Metadata metadata) {
        SongRequest request = new SongRequest();
        request.setId(Math.toIntExact(id));
        request.setArtist(metadata.get("xmpDM:artist"));
        request.setName(metadata.get("title"));
        request.setAlbum(metadata.get("xmpDM:album"));
        request.setYear(metadata.get("xmpDM:releaseDate"));
        request.setDuration(formatDuration(metadata.get("xmpDM:duration")));
        return request;
    }

    private String formatDuration(String durationInMillis) {
        if (durationInMillis != null && !durationInMillis.isEmpty()) {
            double milliseconds = Double.parseDouble(durationInMillis);
            long seconds = (long) (milliseconds / 1000) % 60;
            long minutes = (long) (milliseconds / (1000 * 60)) % 60;
            return String.format("%02d:%02d", minutes, seconds);
        }
        return null;
    }

}