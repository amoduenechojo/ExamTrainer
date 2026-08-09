package com.postutmetrainer.controller;

import com.postutmetrainer.security.dto.*;
import com.postutmetrainer.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register/student")
    public AuthResponse registerStudent(@Valid @RequestBody RegisterStudentRequest request) {
        return authService.registerStudent(request);
    }

    @PostMapping("/register/parent")
    public AuthResponse registerParent(@Valid @RequestBody RegisterParentRequest request) {
        return authService.registerParent(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
