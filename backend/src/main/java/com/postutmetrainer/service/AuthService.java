package com.postutmetrainer.service;

import com.postutmetrainer.exception.ApiException;
import com.postutmetrainer.model.Parent;
import com.postutmetrainer.model.Role;
import com.postutmetrainer.model.Student;
import com.postutmetrainer.model.User;
import com.postutmetrainer.repository.ParentRepository;
import com.postutmetrainer.repository.StudentRepository;
import com.postutmetrainer.repository.UserRepository;
import com.postutmetrainer.security.dto.*;
import com.postutmetrainer.security.service.JwtService;
import com.postutmetrainer.security.service.PostutmeTrainerUserDetailsService;
import java.security.SecureRandom;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final String INVITE_CODE_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // no O/0/I/1
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final ParentRepository parentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final PostutmeTrainerUserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UserRepository userRepository,
            StudentRepository studentRepository,
            ParentRepository parentRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            PostutmeTrainerUserDetailsService userDetailsService,
            AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.parentRepository = parentRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse registerStudent(RegisterStudentRequest request) {
        User user = createUser(request.email(), request.password(), Role.STUDENT);

        Student student = Student.builder()
                .user(user)
                .fullName(request.fullName())
                .inviteCode(generateUniqueInviteCode())
                .build();
        studentRepository.save(student);

        String token = issueToken(user);
        return new AuthResponse(token, Role.STUDENT.name(), student.getId(), student.getFullName(), student.getInviteCode());
    }

    @Transactional
    public AuthResponse registerParent(RegisterParentRequest request) {
        User user = createUser(request.email(), request.password(), Role.PARENT);

        Parent parent = Parent.builder()
                .user(user)
                .fullName(request.fullName())
                .build();
        parentRepository.save(parent);

        String token = issueToken(user);
        return new AuthResponse(token, Role.PARENT.name(), parent.getId(), parent.getFullName(), null);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        String token = issueToken(user);

        if (user.getRole() == Role.STUDENT) {
            Student student = studentRepository.findByUser_Email(user.getEmail())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Student profile not found"));
            return new AuthResponse(token, Role.STUDENT.name(), student.getId(), student.getFullName(), student.getInviteCode());
        }

        Parent parent = parentRepository.findByUser_Email(user.getEmail())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Parent profile not found"));
        return new AuthResponse(token, Role.PARENT.name(), parent.getId(), parent.getFullName(), null);
    }

    private User createUser(String email, String rawPassword, Role role) {
        if (userRepository.existsByEmail(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "An account with this email already exists");
        }
        User user = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .role(role)
                .build();
        return userRepository.save(user);
    }

    private String issueToken(User user) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        return jwtService.generateToken(userDetails);
    }

    private String generateUniqueInviteCode() {
        String code;
        do {
            StringBuilder sb = new StringBuilder(8);
            for (int i = 0; i < 8; i++) {
                sb.append(INVITE_CODE_ALPHABET.charAt(RANDOM.nextInt(INVITE_CODE_ALPHABET.length())));
            }
            code = sb.toString();
        } while (studentRepository.existsByInviteCode(code));
        return code;
    }
}
