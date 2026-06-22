package com.mitwpu.cseconnect.controller;

import com.mitwpu.cseconnect.dto.request.CreateAnnouncementRequest;
import com.mitwpu.cseconnect.dto.response.AnnouncementResponse;
import com.mitwpu.cseconnect.dto.response.ApiResponse;
import com.mitwpu.cseconnect.entity.User;
import com.mitwpu.cseconnect.service.AnnouncementService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AnnouncementResponse>>> getVisible(
            @AuthenticationPrincipal User user) {
        List<AnnouncementResponse> response = announcementService.getVisible(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Announcements retrieved", response));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> create(
            @Valid @RequestBody CreateAnnouncementRequest request,
            @AuthenticationPrincipal User user,
            HttpServletRequest httpRequest) {
        AnnouncementResponse response = announcementService.create(request, user.getId(), getIp(httpRequest));
        return ResponseEntity.ok(ApiResponse.success("Announcement created", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            HttpServletRequest httpRequest) {
        announcementService.delete(id, user.getId(), getIp(httpRequest));
        return ResponseEntity.ok(ApiResponse.success("Announcement deleted", null));
    }

    private String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
