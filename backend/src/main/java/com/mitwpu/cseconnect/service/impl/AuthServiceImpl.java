package com.mitwpu.cseconnect.service.impl;

import com.mitwpu.cseconnect.dto.request.LoginRequest;
import com.mitwpu.cseconnect.dto.response.AuthResponse;
import com.mitwpu.cseconnect.entity.AuditLog;
import com.mitwpu.cseconnect.entity.Student;
import com.mitwpu.cseconnect.entity.Teacher;
import com.mitwpu.cseconnect.entity.User;
import com.mitwpu.cseconnect.exception.BadRequestException;
import com.mitwpu.cseconnect.exception.ResourceNotFoundException;
import com.mitwpu.cseconnect.repository.StudentRepository;
import com.mitwpu.cseconnect.repository.TeacherRepository;
import com.mitwpu.cseconnect.repository.UserRepository;
import com.mitwpu.cseconnect.security.JwtUtil;
import com.mitwpu.cseconnect.service.AuditService;
import com.mitwpu.cseconnect.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final AuditService auditService;

    @Override
    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            User user = (User) authentication.getPrincipal();

            if (user.getIsDeleted() != null && user.getIsDeleted()) {
                throw new BadRequestException("Account has been deactivated");
            }

            String accessToken = jwtUtil.generateAccessToken(user, user.getRole().name());
            String refreshToken = jwtUtil.generateRefreshToken(user);

            String name = resolveUserName(user);
            String studentPrn = null;
            if (user.getRole() == User.Role.STUDENT) {
                studentPrn = studentRepository.findByUserId(user.getId())
                        .map(Student::getPrn)
                        .orElse(null);
            }

            String ip = getClientIp(httpRequest);
            auditService.log(user.getId(), name, user.getRole().name(),
                    AuditLog.AuditAction.LOGIN, "User", user.getId(),
                    null, null, "User logged in", ip);

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .userId(user.getId())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .name(name)
                    .studentPrn(studentPrn)
                    .build();
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid email or password");
        }
    }

    @Override
    public AuthResponse refresh(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken) || jwtUtil.isTokenExpired(refreshToken)) {
            throw new BadRequestException("Invalid or expired refresh token");
        }

        String email = jwtUtil.extractEmail(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String accessToken = jwtUtil.generateAccessToken(user, user.getRole().name());
        String newRefreshToken = jwtUtil.generateRefreshToken(user);
        String name = resolveUserName(user);
        String studentPrn = null;
        if (user.getRole() == User.Role.STUDENT) {
            studentPrn = studentRepository.findByUserId(user.getId())
                    .map(Student::getPrn)
                    .orElse(null);
        }

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .name(name)
                .studentPrn(studentPrn)
                .build();
    }

    @Override
    public void logout(String token, HttpServletRequest httpRequest) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (token != null && jwtUtil.validateToken(token)) {
            String email = jwtUtil.extractEmail(token);
            User user = userRepository.findByEmail(email).orElse(null);
            if (user != null) {
                String name = resolveUserName(user);
                String ip = getClientIp(httpRequest);
                auditService.log(user.getId(), name, user.getRole().name(),
                        AuditLog.AuditAction.LOGOUT, "User", user.getId(),
                        null, null, "User logged out", ip);
            }
        }
    }

    private String resolveUserName(User user) {
        if (user.getRole() == User.Role.STUDENT) {
            return studentRepository.findByUserId(user.getId())
                    .map(Student::getFullName)
                    .orElse(user.getEmail());
        } else if (user.getRole() == User.Role.TEACHER) {
            return teacherRepository.findByUserId(user.getId())
                    .map(Teacher::getFullName)
                    .orElse(user.getEmail());
        }
        return "Admin";
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
