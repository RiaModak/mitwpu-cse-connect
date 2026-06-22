package com.mitwpu.cseconnect.controller;

import com.mitwpu.cseconnect.dto.request.ResetPasswordRequest;
import com.mitwpu.cseconnect.dto.response.ApiResponse;
import com.mitwpu.cseconnect.dto.response.AuditLogResponse;
import com.mitwpu.cseconnect.dto.response.PagedResponse;
import com.mitwpu.cseconnect.entity.User;
import com.mitwpu.cseconnect.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/audit-logs")
    public ResponseEntity<ApiResponse<PagedResponse<AuditLogResponse>>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<AuditLogResponse> response = adminService.getAuditLogs(pageable);
        return ResponseEntity.ok(ApiResponse.success("Audit logs retrieved", response));
    }

    @PutMapping("/students/{prn}/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetStudentPassword(
            @PathVariable String prn,
            @Valid @RequestBody ResetPasswordRequest request,
            @AuthenticationPrincipal User user,
            HttpServletRequest httpRequest) {
        adminService.resetStudentPassword(prn, request, user.getId(), getIp(httpRequest));
        return ResponseEntity.ok(ApiResponse.success("Student password reset", null));
    }

    @PutMapping("/teachers/{id}/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetTeacherPassword(
            @PathVariable Long id,
            @Valid @RequestBody ResetPasswordRequest request,
            @AuthenticationPrincipal User user,
            HttpServletRequest httpRequest) {
        adminService.resetTeacherPassword(id, request, user.getId(), getIp(httpRequest));
        return ResponseEntity.ok(ApiResponse.success("Teacher password reset", null));
    }

    private String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
