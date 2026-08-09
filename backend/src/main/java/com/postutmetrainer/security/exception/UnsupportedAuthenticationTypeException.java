package com.postutmetrainer.security.exception;

/**
 * Thrown by PostutmeTrainerAuthenticationManager when none of its providers support the
 * Authentication implementation it was asked to authenticate. Today we only ever hand it a
 * UsernamePasswordAuthenticationToken, so this is a defensive guard rather than something that
 * should fire in normal use — it exists so a future auth type added without a matching provider
 * fails loudly instead of silently falling through.
 */
public class UnsupportedAuthenticationTypeException extends AppSecurityException {

    public UnsupportedAuthenticationTypeException(Class<?> authenticationType) {
        super("No AuthenticationProvider is registered for authentication type: " + authenticationType.getName());
    }
}
