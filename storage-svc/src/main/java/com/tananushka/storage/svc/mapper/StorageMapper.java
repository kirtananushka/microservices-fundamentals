package com.tananushka.storage.svc.mapper;

import com.tananushka.storage.svc.dto.StorageDto;
import com.tananushka.storage.svc.entity.Storage;
import org.springframework.stereotype.Component;

@Component
public class StorageMapper {

    public StorageDto toDto(Storage storage) {
        StorageDto storageDto = new StorageDto();
        storageDto.setId(storage.getId());
        storageDto.setStorageType(storage.getStorageType());
        storageDto.setBucket(storage.getBucket());
        storageDto.setPath(storage.getPath());
        return storageDto;
    }

    public Storage toEntity(StorageDto storageDto) {
        Storage storage = new Storage();
        storage.setId(storageDto.getId());
        storage.setStorageType(storageDto.getStorageType());
        storage.setBucket(storageDto.getBucket());
        storage.setPath(storageDto.getPath());
        return storage;
    }
}
