package com.learning.blog.service.impl;

import com.learning.blog.mapper.UserMapper;
import com.learning.blog.model.User;
import com.learning.blog.model.dtos.*;
import com.learning.blog.repository.UserRepository;
import com.learning.blog.service.AuthenticationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final EmailService emailService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        try {
            if (userRepository.existsByEmail(registerRequest.getEmail())) {
                log.warn("Registration attempt with existing email: {}", registerRequest.getEmail());
                throw new IllegalArgumentException("Email already in use");
            }

            if(!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
                log.warn("Password and confirm password do not match for email: {}", registerRequest.getEmail());
                throw new IllegalArgumentException("Password and confirm password do not match");
            }

            User user = userMapper.toEntity(registerRequest);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setEnabled(false);
            user.setVerificationCode(generateVerificationCode());
            user.setExpirationTime(LocalDateTime.now().plusMinutes(15));
            User savedUser = userRepository.save(user);

            emailService.sendVerificationEmail(savedUser);

            return AuthResponse.builder()
                    .statusCode(201)
                    .message("User registered successfully. Please verify your email to activate your account.")
                    .build();

        } catch (IllegalArgumentException e) {
            log.error("Registration error for email {}: {}", registerRequest.getEmail(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error during registration for email: {}, error: {}", registerRequest.getEmail(), e.getMessage());
            throw e;
        }
    }

    @Override
    public AuthResponse verifyAccount(VerifyRequest verifyRequest) {
        try {
            String email = verifyRequest.getEmail();
            String code = verifyRequest.getVerificationCode();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
            String token = jwtService.generateToken(userDetails);

            if (user.isEnabled()) {
                return AuthResponse.builder()
                        .statusCode(200)
                        .token(token)
                        .message("Account already verified")
                        .build();
            }

            if (!user.getVerificationCode().equals(code)) {
                throw new IllegalArgumentException("Invalid verification code");
            }

            if (user.getExpirationTime().isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("Verification code expired, please request a new one");
            }

            user.setEnabled(true);
            user.setVerificationCode(null);
            user.setExpirationTime(null);
            userRepository.save(user);

            return AuthResponse.builder()
                    .statusCode(200)
                    .token(token)
                    .message("Account verified successfully")
                    .build();

        } catch (Exception e) {
            log.error("Error during account verification for email: {}, error: {}", verifyRequest.getEmail(), e.getMessage());
            throw e;
        }
    }

    @Override
    public AuthResponse resendVerification(String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

            if (user.isEnabled()) {
                return AuthResponse.builder()
                        .statusCode(200)
                        .message("Account already verified")
                        .build();
            }

            user.setVerificationCode(generateVerificationCode());
            user.setExpirationTime(LocalDateTime.now().plusMinutes(15));
            userRepository.save(user);

            emailService.sendVerificationEmail(user);

            return AuthResponse.builder()
                    .statusCode(200)
                    .message("Verification code resent successfully")
                    .build();

        } catch (Exception e) {
            log.error("Error during resending verification for email: {}, error: {}", email, e.getMessage());
            throw e;
        }
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + loginRequest.getEmail()));

            if (!user.isEnabled()) {
                throw new IllegalArgumentException("User is not enabled. Please verify your account first.");
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
            String token = jwtService.generateToken(userDetails);

            return AuthResponse.builder()
                    .statusCode(200)
                    .token(token)
                    .message("Login successful")
                    .build();

        } catch (BadCredentialsException e) {
            log.error("Authentication failed for email: {}", loginRequest.getEmail());
            throw e;
        } catch (Exception e) {
            log.error("Error during authentication for email: {}, error: {}", loginRequest.getEmail(), e.getMessage());
            throw e;
        }
    }

    @Override
    public AuthResponse forgotPassword(String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

            user.setVerificationCode(generateVerificationCode());
            user.setExpirationTime(LocalDateTime.now().plusMinutes(15));
            User savedUser = userRepository.save(user);

            emailService.sendResetPasswordEmail(savedUser);

            return AuthResponse.builder()
                    .statusCode(200)
                    .message("Password reset code sent successfully")
                    .build();

        } catch (Exception e) {
            log.error("Error during forgot password for email: {}, error: {}", email, e.getMessage());
            throw e;
        }
    }

    @Override
    public AuthResponse resetPassword(ResetPasswordRequest resetPasswordRequest) {
        try {
            String email = resetPasswordRequest.getEmail();
            String code = resetPasswordRequest.getPasswordResetCode();
            String newPassword = resetPasswordRequest.getNewPassword();
            String confirmPassword = resetPasswordRequest.getConfirmPassword();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

            if (!user.isEnabled()) {
                throw new IllegalArgumentException("Account is not verified, please verify your account first");
            }

            if (!user.getVerificationCode().equals(code)) {
                throw new IllegalArgumentException("Invalid password reset code");
            }

            if (user.getExpirationTime().isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("Password reset code expired, please request a new one");
            }

            if (!newPassword.equals(confirmPassword)) {
                throw new IllegalArgumentException("New password and confirm password do not match");
            }

            user.setPassword(passwordEncoder.encode(newPassword));
            user.setVerificationCode(null);
            user.setExpirationTime(null);
            userRepository.save(user);

            return AuthResponse.builder()
                    .statusCode(200)
                    .message("Password reset successfully")
                    .build();

        } catch (Exception e) {
            log.error("Error during password reset for email: {}, error: {}", resetPasswordRequest.getEmail(), e.getMessage());
            throw e;
        }
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        try {
            String email = refreshTokenRequest.getEmail();
            String token = refreshTokenRequest.getToken();

            if (email == null || token == null) {
                log.warn("Email or token is null in refresh request");
                throw new IllegalArgumentException("Email and token must be provided");
            }

            if (!userRepository.existsByEmail(email)) {
                throw new IllegalArgumentException("Email not found");
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            if (jwtService.isTokenValid(token, userDetails)) {
                String newToken = jwtService.generateToken(userDetails);
                return AuthResponse.builder()
                        .statusCode(200)
                        .token(newToken)
                        .message("Token refreshed successfully")
                        .build();
            } else {
                log.warn("Invalid token for email: {}", email);
                throw new IllegalArgumentException("Invalid token");
            }

        } catch (Exception e) {
            log.error("Error during token refresh: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public AuthResponse logout() {
        return AuthResponse.builder()
                .statusCode(200)
                .message("Logout successful")
                .build();
    }

    private String generateVerificationCode() {
        return String.valueOf((int) ((Math.random() * 900000) + 100000));
    }
}
