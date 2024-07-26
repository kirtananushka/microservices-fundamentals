package com.tananushka.storage.svc.service;

import com.tananushka.storage.svc.dto.StorageDto;
import com.tananushka.storage.svc.entity.Storage;
import com.tananushka.storage.svc.mapper.StorageMapper;
import com.tananushka.storage.svc.repository.StorageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageService {
    private final StorageRepository storageRepository;
    private final StorageMapper storageMapper;

    @Transactional
    public StorageDto createStorage(StorageDto storageDto) {
        replacingExistingStorage(storageDto.getStorageType());
        Storage storage = storageMapper.toEntity(storageDto);
        Storage savedStorage = storageRepository.save(storage);
        log.info("New storage object created: bucket name={}, path={}", savedStorage.getBucket(), savedStorage.getPath());
        return storageMapper.toDto(savedStorage);
    }

    public List<StorageDto> getAllStorages() {
        List<StorageDto> storages = storageRepository.findAll().stream()
                .map(storageMapper::toDto)
                .toList();
        log.info("Retrieved {} storages", storages.size());
        return storages;
    }

    @Transactional
    public List<Long> deleteStorages(List<Long> ids) {
        List<Storage> storagesToDelete = storageRepository.findAllById(ids);
        List<Long> existingIds = storagesToDelete.stream().map(Storage::getId).toList();
        storageRepository.deleteAllById(existingIds);
        log.info("Deleted storages with IDs {}", existingIds);
        return existingIds;
    }

    private void replacingExistingStorage(String storageType) {
        Optional<Storage> existingStorage = storageRepository.findByStorageType(storageType);
        existingStorage.ifPresent(storage -> {
            log.info("Replacing existing storage with type: {}", storageType);
            storageRepository.delete(storage);
        });
    }
}
