package com.postutmetrainer.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Base type for failures raised inside our own security layer (provider, manager, filter),
 * as opposed to Spring Security's built-in exceptions. Extends AuthenticationException so it
 * flows through Spring Security's normal authentication error handling untouched.
 */
public class AppSecurityException extends AuthenticationException {

    public AppSecurityException(String message) {
        super(message);
    }

    public AppSecurityException(String message, Throwable cause) {
        super(message, cause);
    }
}
