package com.learning.blog.service;

import com.learning.blog.model.dtos.*;

public interface AuthenticationService {
    AuthResponse register(RegisterRequest registerRequest);
    AuthResponse verifyAccount(VerifyRequest verifyRequest);
    AuthResponse resendVerification(String email);
    AuthResponse login(LoginRequest loginRequest);
    AuthResponse forgotPassword(String email);
    AuthResponse resetPassword(ResetPasswordRequest resetPasswordRequest);
    AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
    AuthResponse logout();
}
