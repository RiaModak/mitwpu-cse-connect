package com.mitwpu.cseconnect.service;

import com.mitwpu.cseconnect.dto.request.LoginRequest;
import com.mitwpu.cseconnect.dto.response.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    AuthResponse login(LoginRequest request, HttpServletRequest httpRequest);
    AuthResponse refresh(String refreshToken);
    void logout(String token, HttpServletRequest httpRequest);
}
