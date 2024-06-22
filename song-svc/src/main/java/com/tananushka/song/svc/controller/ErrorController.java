package com.tananushka.song.svc.controller;

import com.tananushka.song.svc.dto.ErrorDto;
import com.tananushka.song.svc.exception.SongServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorController {

    @ExceptionHandler(SongServiceException.class)
    public ResponseEntity<ErrorDto> songServiceExceptionHandler(SongServiceException e) {
        ErrorDto errorDto = new ErrorDto(e.getMessage(), e.getErrorCode());
        HttpStatus httpStatus = determineHttpStatus(e.getErrorCode());
        logError(errorDto, e);
        return new ResponseEntity<>(errorDto, httpStatus);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
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
