package com.tananushka.resource.svc.service;

import com.tananushka.resource.svc.dto.ServiceInstanceDto;
import lombok.AllArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class DiscoveryService {

    private final DiscoveryClient discoveryClient;

    public List<ServiceInstanceDto> getAllAvailableInstances() {
        List<ServiceInstanceDto> serviceInstanceDtos = new ArrayList<>();

        List<String> services = discoveryClient.getServices();
        for (String service : services) {
            List<ServiceInstance> instances = discoveryClient.getInstances(service);
            for (ServiceInstance instance : instances) {
                ServiceInstanceDto dto = new ServiceInstanceDto();
                dto.setServiceName(service);
                dto.setUri(instance.getUri().toString());
                dto.setHost(instance.getHost());
                dto.setPort(instance.getPort());
                dto.setServiceId(instance.getServiceId());
                dto.setInstanceId(instance.getInstanceId());
                dto.setScheme(instance.getScheme());
                dto.setMetadata(instance.getMetadata());

                serviceInstanceDtos.add(dto);
            }
        }

        return serviceInstanceDtos;
    }
}
