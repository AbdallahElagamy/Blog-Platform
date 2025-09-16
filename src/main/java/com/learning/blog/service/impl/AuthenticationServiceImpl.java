package com.learning.blog.service.impl;

import com.learning.blog.mapper.UserMapper;
import com.learning.blog.model.User;
import com.learning.blog.model.dtos.AuthResponse;
import com.learning.blog.model.dtos.LoginRequest;
import com.learning.blog.model.dtos.RegisterRequest;
import com.learning.blog.repository.UserRepository;
import com.learning.blog.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    @Override
    public AuthResponse register(RegisterRequest registerRequest) {
        try {
            if (userRepository.existsByEmail(registerRequest.getEmail())) {
                log.warn("Registration attempt with existing email: {}", registerRequest.getEmail());
                throw new IllegalArgumentException("Email already in use");
            }

            User user = userMapper.toEntity(registerRequest);
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            User savedUser = userRepository.save(user);
            log.info("User registered successfully with ID: {} and email: {}", savedUser.getId(), savedUser.getEmail());

            UserDetails userDetails = userDetailsService.loadUserByUsername(registerRequest.getEmail());
            String token = jwtService.generateToken(userDetails);

            return AuthResponse.builder()
                    .statusCode(201)
                    .token(token)
                    .message("User registered successfully")
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
    public AuthResponse login(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

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
}
