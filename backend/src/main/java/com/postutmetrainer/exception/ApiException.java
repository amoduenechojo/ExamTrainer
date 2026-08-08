package com.postutmetrainer.exception;

import org.springframework.http.HttpStatus;

/** Thrown for any expected business-rule failure (not found, conflict, unauthorized, etc). */
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
