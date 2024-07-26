package com.tananushka.storage.svc.repository;

import com.tananushka.storage.svc.entity.Storage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StorageRepository extends JpaRepository<Storage, Long> {
    Optional<Storage> findByStorageType(String storageType);
}
