package com.tananushka.resource.svc.controller;

import com.tananushka.resource.svc.dto.ServiceInstanceDto;
import com.tananushka.resource.svc.service.DiscoveryService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/discovery")
@AllArgsConstructor
public class DiscoveryController {

    private final DiscoveryService discoveryService;

    @GetMapping("/services")
    public List<ServiceInstanceDto> getAllServices() {
        return discoveryService.getAllAvailableInstances();
    }
}
