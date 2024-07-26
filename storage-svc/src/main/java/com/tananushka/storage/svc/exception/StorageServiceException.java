package com.tananushka.storage.svc.exception;

import lombok.Getter;

@Getter
public class StorageServiceException extends RuntimeException {
    private final String errorCode;

    public StorageServiceException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public StorageServiceException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
