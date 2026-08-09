package com.postutmetrainer.security.config;

import com.postutmetrainer.security.manager.PostutmeTrainerAuthenticationManager;
import com.postutmetrainer.security.provider.PostutmeTrainerAuthenticationProvider;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Beans that back the security layer itself (hashing, the AuthenticationManager), kept separate
 * from SecurityConfig so the filter chain / CORS wiring in SecurityConfig isn't cluttered with
 * bean definitions that don't belong to "what does the HTTP request pipeline look like".
 */
@Configuration
public class SecureBeanConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(PostutmeTrainerAuthenticationProvider provider) {
        return new PostutmeTrainerAuthenticationManager(List.of(provider));
    }
}
