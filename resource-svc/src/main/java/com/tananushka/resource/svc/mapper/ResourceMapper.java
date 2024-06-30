package com.tananushka.resource.svc.mapper;

import com.tananushka.resource.svc.dto.ResourceResponse;
import com.tananushka.resource.svc.entity.Resource;
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
}