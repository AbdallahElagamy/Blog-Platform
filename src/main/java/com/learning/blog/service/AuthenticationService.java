package com.learning.blog.service;

import com.learning.blog.model.dtos.AuthResponse;
import com.learning.blog.model.dtos.LoginRequest;
import com.learning.blog.model.dtos.RegisterRequest;

public interface AuthenticationService {
    AuthResponse register(RegisterRequest registerRequest);
    AuthResponse login(LoginRequest loginRequest);
}
