package com.postutmetrainer.security.provider;

import com.postutmetrainer.security.service.PostutmeTrainerUserDetailsService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Validates an email/password login against our own UserDetailsService and PasswordEncoder.
 * This replaces Spring's default DaoAuthenticationProvider so the check is explicit and easy
 * to extend later (e.g. account lockout, login attempt tracking).
 */
@Component
public class PostutmeTrainerAuthenticationProvider implements AuthenticationProvider {

    private final PostutmeTrainerUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public PostutmeTrainerAuthenticationProvider(
            PostutmeTrainerUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String rawPassword = String.valueOf(authentication.getCredentials());

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if (!passwordEncoder.matches(rawPassword, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authenticationType) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authenticationType);
    }
}
