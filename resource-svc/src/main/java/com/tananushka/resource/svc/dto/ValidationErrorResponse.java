package com.tananushka.resource.svc.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ValidationErrorResponse {

    private Map<String, String> errors;
}
