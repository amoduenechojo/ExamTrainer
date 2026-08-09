package com.postutmetrainer.security.manager;

import com.postutmetrainer.security.exception.UnsupportedAuthenticationTypeException;
import java.util.List;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * Picks the first registered AuthenticationProvider that supports the given Authentication type
 * and delegates to it. There's only one provider today (username/password), but this keeps the
 * door open to add others (e.g. a future OAuth provider) without touching this class's callers.
 */
@Component
public class PostutmeTrainerAuthenticationManager implements AuthenticationManager {

    private final List<AuthenticationProvider> providers;

    public PostutmeTrainerAuthenticationManager(List<AuthenticationProvider> providers) {
        this.providers = providers;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return providers.stream()
                .filter(provider -> provider.supports(authentication.getClass()))
                .findFirst()
                .orElseThrow(() -> new UnsupportedAuthenticationTypeException(authentication.getClass()))
                .authenticate(authentication);
    }
}
