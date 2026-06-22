package com.mitwpu.cseconnect.controller;

import com.mitwpu.cseconnect.dto.request.SubmitAchievementRequest;
import com.mitwpu.cseconnect.dto.request.VerifyAchievementRequest;
import com.mitwpu.cseconnect.dto.response.AchievementResponse;
import com.mitwpu.cseconnect.dto.response.ApiResponse;
import com.mitwpu.cseconnect.dto.response.PagedResponse;
import com.mitwpu.cseconnect.entity.Student;
import com.mitwpu.cseconnect.entity.User;
import com.mitwpu.cseconnect.exception.ResourceNotFoundException;
import com.mitwpu.cseconnect.repository.StudentRepository;
import com.mitwpu.cseconnect.service.AchievementService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/achievements")
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementService achievementService;
    private final StudentRepository studentRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<AchievementResponse>>> getAll(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<AchievementResponse> response = achievementService.getAll(category, status, pageable);
        return ResponseEntity.ok(ApiResponse.success("Achievements retrieved", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AchievementResponse>> getById(@PathVariable Long id) {
        AchievementResponse response = achievementService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Achievement retrieved", response));
    }

    @GetMapping("/student/{prn}")
    public ResponseEntity<ApiResponse<List<AchievementResponse>>> getByStudent(@PathVariable String prn) {
        List<AchievementResponse> response = achievementService.getByStudent(prn);
        return ResponseEntity.ok(ApiResponse.success("Achievements retrieved", response));
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<AchievementResponse>> submit(
            @Valid @ModelAttribute SubmitAchievementRequest request,
            @RequestParam(value = "proofFile", required = false) MultipartFile proofFile,
            @AuthenticationPrincipal User user) {
        Student student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        AchievementResponse response = achievementService.submit(request, proofFile, student.getId());
        return ResponseEntity.ok(ApiResponse.success("Achievement submitted", response));
    }

    @PutMapping("/{id}/verify")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<AchievementResponse>> verify(
            @PathVariable Long id,
            @Valid @RequestBody VerifyAchievementRequest request,
            @AuthenticationPrincipal User user,
            HttpServletRequest httpRequest) {
        AchievementResponse response = achievementService.verify(id, request, user.getId(), getIp(httpRequest));
        return ResponseEntity.ok(ApiResponse.success("Achievement updated", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            HttpServletRequest httpRequest) {
        achievementService.delete(id, user.getId(), getIp(httpRequest));
        return ResponseEntity.ok(ApiResponse.success("Achievement deleted", null));
    }

    private String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
