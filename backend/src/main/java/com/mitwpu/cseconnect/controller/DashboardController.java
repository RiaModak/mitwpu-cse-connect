package com.mitwpu.cseconnect.controller;

import com.mitwpu.cseconnect.dto.response.ApiResponse;
import com.mitwpu.cseconnect.dto.response.DashboardStatsResponse;
import com.mitwpu.cseconnect.entity.User;
import com.mitwpu.cseconnect.service.AdminService;
import com.mitwpu.cseconnect.service.StudentService;
import com.mitwpu.cseconnect.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final AdminService adminService;
    private final TeacherService teacherService;
    private final StudentService studentService;

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> adminDashboard() {
        DashboardStatsResponse response = adminService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success("Dashboard stats retrieved", response));
    }

    @GetMapping("/teacher")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> teacherDashboard(
            @AuthenticationPrincipal User user) {
        DashboardStatsResponse response = teacherService.getTeacherDashboard(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Dashboard stats retrieved", response));
    }

    @GetMapping("/student")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> studentDashboard(
            @AuthenticationPrincipal User user) {
        DashboardStatsResponse response = studentService.getStudentDashboard(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Dashboard stats retrieved", response));
    }
}
