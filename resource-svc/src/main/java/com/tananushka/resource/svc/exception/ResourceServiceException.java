package com.tananushka.resource.svc.exception;

import lombok.Getter;

@Getter
public class ResourceServiceException extends RuntimeException {

    private final String errorCode;

    public ResourceServiceException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ResourceServiceException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
