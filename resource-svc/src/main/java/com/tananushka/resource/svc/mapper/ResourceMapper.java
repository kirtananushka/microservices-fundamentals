package com.tananushka.resource.svc.mapper;

import com.tananushka.resource.svc.dto.ResourceResponse;
import com.tananushka.resource.svc.entity.Resource;
import org.springframework.stereotype.Component;

@Component
public class ResourceMapper {
    public ResourceResponse toResponse(Resource resource) {
        ResourceResponse response = new ResourceResponse();
        response.setId(Math.toIntExact(resource.getId()));
        response.setS3Location(resource.getS3Location());
        response.setStorageType(resource.getStorageType());
        return response;
    }
}