package com.tananushka.resource.svc.dto;

import lombok.Data;

@Data
public class ResourceResponse {
    private Integer id;
    private String s3Location;
    private String storageType;
}
