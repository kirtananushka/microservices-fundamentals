package com.tananushka.resource.svc.controller;

import com.tananushka.resource.svc.dto.ErrorDto;
import com.tananushka.resource.svc.exception.ResourceServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@RestControllerAdvice
public class ErrorController {

    @ExceptionHandler(ResourceServiceException.class)
    public ResponseEntity<ErrorDto> resourceServiceExceptionHandler(ResourceServiceException e) {
        ErrorDto errorDto = new ErrorDto(e.getMessage(), e.getErrorCode());
        HttpStatus httpStatus = determineHttpStatus(e.getErrorCode());
        logError(errorDto, e);
        return new ResponseEntity<>(errorDto, httpStatus);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorDto> handleHttpClientErrorException(HttpClientErrorException e) {
        if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
            String errorMessage = e.getResponseBodyAsString();
            ErrorDto errorDto = new ErrorDto(errorMessage, "400");
            logError(errorDto, e);
            return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
        } else {
            ErrorDto errorDto = new ErrorDto(e.getMessage(), e.getStatusCode().toString());
            logError(errorDto, e);
            return new ResponseEntity<>(errorDto, e.getStatusCode());
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> exceptionHandler(Exception e) {
        ErrorDto errorDto = new ErrorDto(e.getMessage(), "500");
        logError(errorDto, e);
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void logError(ErrorDto errorDto, Throwable throwable) {
        String message = throwable.getCause() != null ?
                String.format("Error occurred: errorMessage=%s, errorCode=%s, causeMessage=%s", errorDto.errorMessage(),
                        errorDto.errorCode(), throwable.getCause().getMessage()) :
                String.format("Error occurred: errorMessage=%s, errorCode=%s", errorDto.errorMessage(),
                        errorDto.errorCode());
        log.error(message, throwable);
    }

    private HttpStatus determineHttpStatus(String errorCode) {
        try {
            int statusCode = Integer.parseInt(errorCode);
            return HttpStatus.resolve(statusCode) != null ?
                    HttpStatus.resolve(statusCode) :
                    HttpStatus.INTERNAL_SERVER_ERROR;
        } catch (NumberFormatException e) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
