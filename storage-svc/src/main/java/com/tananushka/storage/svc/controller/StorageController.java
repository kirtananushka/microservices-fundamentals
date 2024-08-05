package com.tananushka.storage.svc.controller;

import com.tananushka.storage.svc.dto.StorageDto;
import com.tananushka.storage.svc.service.StorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/storages")
@RequiredArgsConstructor
@Slf4j
public class StorageController {
    private final StorageService storageService;

    @PostMapping
    public ResponseEntity<StorageDto> createStorage(@Valid @RequestBody StorageDto storageDto) {
        StorageDto createdStorage = storageService.createStorage(storageDto);
        return ResponseEntity.ok(createdStorage);
    }

    @GetMapping
    public ResponseEntity<List<StorageDto>> getAllStorages() {
        List<StorageDto> storages = storageService.getAllStorages();
        return ResponseEntity.ok(storages);
    }

    @DeleteMapping
    public ResponseEntity<Map<String, List<Long>>> deleteStorages(@RequestParam("ids") String csvIds) {
        List<Long> ids = Arrays.stream(csvIds.split(","))
                .map(Long::parseLong)
                .toList();
        List<Long> deletedIds = storageService.deleteStorages(ids);
        return ResponseEntity.ok(Map.of("ids", deletedIds));
    }
}
