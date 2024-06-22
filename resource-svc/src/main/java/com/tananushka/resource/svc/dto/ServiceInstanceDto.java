package com.tananushka.resource.svc.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ServiceInstanceDto {

    private String serviceName;

    private String uri;

    private String host;

    private int port;

    private String serviceId;

    private String instanceId;

    private String scheme;

    private Map<String, String> metadata;
}
