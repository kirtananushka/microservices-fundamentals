package com.tananushka.resource.proc.exception;

public class ResourceProcessorException extends RuntimeException {

    private final String errorCode;

    public ResourceProcessorException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ResourceProcessorException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
