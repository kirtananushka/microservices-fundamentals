package com.tananushka.resource.svc.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class ValidationClientException extends RuntimeException {

    private Map<String, String> validationErrors;

    public ValidationClientException(Map<String, String> validationErrors) {
        super("Validation errors occurred");
        this.validationErrors = validationErrors;
    }

    public ValidationClientException(Throwable cause) {
        super("Validation errors occurred", cause);
    }
}

