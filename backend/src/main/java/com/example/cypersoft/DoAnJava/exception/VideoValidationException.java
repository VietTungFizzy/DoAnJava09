package com.example.cypersoft.DoAnJava.exception;

public class VideoValidationException extends RuntimeException {
    public VideoValidationException(String message) {
        super(message);
    }

    public VideoValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
