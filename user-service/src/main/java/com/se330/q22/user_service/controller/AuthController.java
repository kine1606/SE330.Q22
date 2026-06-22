package com.se330.q22.user_service.controller;

import com.se330.q22.user_service.dto.AuthRequest;
import com.se330.q22.user_service.dto.AuthResponse;
import com.se330.q22.user_service.dto.RegisterRequest;
import com.se330.q22.user_service.service.AuthService;
import com.se330.q22.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.se330.q22.user_service.entity.User;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final UserService userService;
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping
    public List<User> getAll()
    {
        return userService.getAll();
    }
}
