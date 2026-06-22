package com.mitwpu.cseconnect.controller;

import com.mitwpu.cseconnect.dto.request.AssignPanelRequest;
import com.mitwpu.cseconnect.dto.request.CreateTeacherRequest;
import com.mitwpu.cseconnect.dto.request.UpdateTeacherRequest;
import com.mitwpu.cseconnect.dto.response.ApiResponse;
import com.mitwpu.cseconnect.dto.response.StudentSummaryResponse;
import com.mitwpu.cseconnect.dto.response.TeacherResponse;
import com.mitwpu.cseconnect.entity.User;
import com.mitwpu.cseconnect.service.TeacherService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TeacherResponse>>> getAll() {
        List<TeacherResponse> response = teacherService.getAll();
        return ResponseEntity.ok(ApiResponse.success("Teachers retrieved", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TeacherResponse>> getById(@PathVariable Long id) {
        TeacherResponse response = teacherService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Teacher retrieved", response));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TeacherResponse>> create(
            @Valid @RequestBody CreateTeacherRequest request,
            @AuthenticationPrincipal User user,
            HttpServletRequest httpRequest) {
        TeacherResponse response = teacherService.create(request, user.getId(), getIp(httpRequest));
        return ResponseEntity.ok(ApiResponse.success("Teacher created", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TeacherResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTeacherRequest request,
            @AuthenticationPrincipal User user,
            HttpServletRequest httpRequest) {
        TeacherResponse response = teacherService.update(id, request, user.getId(), getIp(httpRequest));
        return ResponseEntity.ok(ApiResponse.success("Teacher updated", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            HttpServletRequest httpRequest) {
        teacherService.delete(id, user.getId(), getIp(httpRequest));
        return ResponseEntity.ok(ApiResponse.success("Teacher deleted", null));
    }

    @PostMapping("/assign-panel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> assignPanel(
            @Valid @RequestBody AssignPanelRequest request,
            @AuthenticationPrincipal User user,
            HttpServletRequest httpRequest) {
        teacherService.assignPanel(request, user.getId(), getIp(httpRequest));
        return ResponseEntity.ok(ApiResponse.success("Panel assigned", null));
    }

    @GetMapping("/my-students")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<StudentSummaryResponse>>> getMyStudents(
            @AuthenticationPrincipal User user) {
        List<StudentSummaryResponse> response = teacherService.getStudentsByTeacher(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Students retrieved", response));
    }

    @GetMapping("/panel/{panel}/students")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<StudentSummaryResponse>>> getStudentsByPanel(
            @PathVariable String panel) {
        List<StudentSummaryResponse> response = teacherService.getStudentsByPanel(panel);
        return ResponseEntity.ok(ApiResponse.success("Students retrieved", response));
    }

    private String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
