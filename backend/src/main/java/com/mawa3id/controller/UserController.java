package com.mawa3id.controller;

import com.mawa3id.dto.AuthResponse;
import com.mawa3id.dto.LoginRequest;
import com.mawa3id.dto.RegisterRequest;
import com.mawa3id.model.User;
import com.mawa3id.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.registerUser(request);
        AuthResponse response = AuthResponse.builder()
                .message("User registered successfully")
                .user(AuthResponse.UserDTO.fromUser(user))
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.loginUser(request));
    }

    @GetMapping("/profile")
    public ResponseEntity<AuthResponse.UserDTO> getProfile(Authentication authentication) {
        UUID userId = (UUID) authentication.getDetails();
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(AuthResponse.UserDTO.fromUser(user));
    }

    @PutMapping("/profile")
    public ResponseEntity<AuthResponse.UserDTO> updateProfile(
            Authentication authentication,
            @Valid @RequestBody RegisterRequest request) {
        UUID userId = (UUID) authentication.getDetails();
        User user = userService.updateUserProfile(userId, request);
        return ResponseEntity.ok(AuthResponse.UserDTO.fromUser(user));
    }
}
