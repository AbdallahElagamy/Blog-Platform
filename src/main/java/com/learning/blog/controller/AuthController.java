package com.learning.blog.controller;

import com.learning.blog.model.dtos.*;
import com.learning.blog.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest registerRequest) {

        AuthResponse authResponse = authenticationService.register(registerRequest);

        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    @PostMapping("/verify")
    public ResponseEntity<AuthResponse> verify(
            @Valid @RequestBody VerifyRequest verifyRequest) {

        AuthResponse authResponse = authenticationService.verifyAccount(verifyRequest);

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<AuthResponse> resendVerification(
            @Valid @RequestBody VerifyRequest verifyRequest) {

        AuthResponse authResponse = authenticationService.resendVerification(verifyRequest.getEmail());

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest loginRequest) {

        AuthResponse authResponse = authenticationService.login(loginRequest);

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<AuthResponse> forgotPassword(@RequestParam String email) {

        AuthResponse authResponse = authenticationService.forgotPassword(email);

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<AuthResponse> resetPassword(
            @Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {

        AuthResponse authResponse = authenticationService.resetPassword(resetPasswordRequest);

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {

        AuthResponse authResponse = authenticationService.refreshToken(refreshTokenRequest);

        return ResponseEntity.ok(authResponse);
    }

    @GetMapping("/logout")
    public ResponseEntity<AuthResponse> logout() {
        AuthResponse authResponse = authenticationService.logout();

        return ResponseEntity.ok(authResponse);
    }
}
