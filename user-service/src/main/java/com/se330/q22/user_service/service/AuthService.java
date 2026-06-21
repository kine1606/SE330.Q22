package com.se330.q22.user_service.service;

import com.se330.q22.user_service.dto.AuthRequest;
import com.se330.q22.user_service.dto.AuthResponse;
import com.se330.q22.user_service.dto.RegisterRequest;
import com.se330.q22.user_service.entity.Role;
import com.se330.q22.user_service.entity.User;
import com.se330.q22.user_service.repository.UserRepository;
import com.se330.q22.user_service.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(Role.USER) // default role
                .build();

        userRepository.save(user);

        String jwtToken = jwtUtils.generateToken(user.getEmail(), user.getId(), user.getRole().name());

        return AuthResponse.builder()
                .token(jwtToken)
                .userId(user.getId())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .build();
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String jwtToken = jwtUtils.generateToken(user.getEmail(), user.getId(), user.getRole().name());

        return AuthResponse.builder()
                .token(jwtToken)
                .userId(user.getId())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .build();
    }
}
