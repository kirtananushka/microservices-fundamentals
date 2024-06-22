package com.tananushka.song.svc.exception;

import lombok.Getter;

@Getter
public class SongServiceException extends RuntimeException {

    private final String errorCode;

    public SongServiceException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public SongServiceException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
