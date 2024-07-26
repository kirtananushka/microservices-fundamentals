package com.tananushka.resource.svc.util;

import com.tananushka.resource.svc.dto.Storage;

import java.util.Arrays;
import java.util.List;

public class StorageFallbackUtil {

    public static List<Storage> getDefaultStorages() {
        return Arrays.asList(
                Storage.builder()
                        .id(1L)
                        .storageType("STAGING")
                        .bucket("fallback-staging-bucket")
                        .path("files")
                        .build(),
                Storage.builder()
                        .id(2L)
                        .storageType("PERMANENT")
                        .bucket("fallback-permanent-bucket")
                        .path("files")
                        .build()
        );
    }
}
