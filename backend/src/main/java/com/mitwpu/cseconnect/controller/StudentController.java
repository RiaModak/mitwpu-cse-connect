package com.mitwpu.cseconnect.controller;

import com.mitwpu.cseconnect.dto.request.AddAcademicRecordRequest;
import com.mitwpu.cseconnect.dto.request.CreateStudentRequest;
import com.mitwpu.cseconnect.dto.request.ResetPasswordRequest;
import com.mitwpu.cseconnect.dto.request.UpdateStudentRequest;
import com.mitwpu.cseconnect.dto.response.*;
import com.mitwpu.cseconnect.entity.Student;
import com.mitwpu.cseconnect.entity.User;
import com.mitwpu.cseconnect.exception.ResourceNotFoundException;
import com.mitwpu.cseconnect.repository.StudentRepository;
import com.mitwpu.cseconnect.service.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final StudentRepository studentRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<StudentSummaryResponse>>> getAll(
            @RequestParam(required = false) String panel,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "fullName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PagedResponse<StudentSummaryResponse> response = studentService.getAll(panel, year, search, pageable);
        return ResponseEntity.ok(ApiResponse.success("Students retrieved", response));
    }

    @GetMapping("/{prn}")
    public ResponseEntity<ApiResponse<StudentDetailResponse>> getByPrn(@PathVariable String prn) {
        StudentDetailResponse response = studentService.getByPrn(prn);
        return ResponseEntity.ok(ApiResponse.success("Student retrieved", response));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<StudentDetailResponse>> create(
            @Valid @RequestBody CreateStudentRequest request,
            @AuthenticationPrincipal User user,
            HttpServletRequest httpRequest) {
        StudentDetailResponse response = studentService.create(request, user.getId(), getIp(httpRequest));
        return ResponseEntity.ok(ApiResponse.success("Student created", response));
    }

    @PutMapping("/{prn}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<StudentDetailResponse>> update(
            @PathVariable String prn,
            @Valid @RequestBody UpdateStudentRequest request,
            @AuthenticationPrincipal User user,
            HttpServletRequest httpRequest) {
        StudentDetailResponse response = studentService.update(prn, request, user.getId(), getIp(httpRequest));
        return ResponseEntity.ok(ApiResponse.success("Student updated", response));
    }

    @DeleteMapping("/{prn}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable String prn,
            @AuthenticationPrincipal User user,
            HttpServletRequest httpRequest) {
        studentService.delete(prn, user.getId(), getIp(httpRequest));
        return ResponseEntity.ok(ApiResponse.success("Student deleted", null));
    }

    @PutMapping("/{prn}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @PathVariable String prn,
            @Valid @RequestBody ResetPasswordRequest request,
            @AuthenticationPrincipal User user,
            HttpServletRequest httpRequest) {
        studentService.resetPassword(prn, request.getNewPassword(), user.getId(), getIp(httpRequest));
        return ResponseEntity.ok(ApiResponse.success("Password reset successful", null));
    }

    @GetMapping("/{prn}/clubs")
    public ResponseEntity<ApiResponse<List<MembershipResponse>>> getClubHistory(@PathVariable String prn) {
        List<MembershipResponse> response = studentService.getClubHistory(prn);
        return ResponseEntity.ok(ApiResponse.success("Club history retrieved", response));
    }

    @GetMapping("/{prn}/achievements")
    public ResponseEntity<ApiResponse<List<AchievementResponse>>> getAchievements(@PathVariable String prn) {
        List<AchievementResponse> response = studentService.getAchievements(prn);
        return ResponseEntity.ok(ApiResponse.success("Achievements retrieved", response));
    }

    @GetMapping("/{prn}/timeline")
    public ResponseEntity<ApiResponse<List<TimelineItemResponse>>> getTimeline(@PathVariable String prn) {
        List<TimelineItemResponse> response = studentService.getTimeline(prn);
        return ResponseEntity.ok(ApiResponse.success("Timeline retrieved", response));
    }

    @PostMapping("/bulk-import")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BulkImportResultResponse>> bulkImport(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user,
            HttpServletRequest httpRequest) {
        BulkImportResultResponse response = studentService.bulkImport(file, user.getId(), getIp(httpRequest));
        return ResponseEntity.ok(ApiResponse.success("Bulk import completed", response));
    }

    @PostMapping("/{prn}/academic-records")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<AcademicRecordResponse>> addAcademicRecord(
            @PathVariable String prn,
            @Valid @RequestBody AddAcademicRecordRequest request,
            @AuthenticationPrincipal User user,
            HttpServletRequest httpRequest) {
        AcademicRecordResponse response = studentService.addAcademicRecord(prn, request, user.getId(), getIp(httpRequest));
        return ResponseEntity.ok(ApiResponse.success("Academic record added", response));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<StudentDetailResponse>> updateSelf(
            @Valid @RequestBody UpdateStudentRequest request,
            @AuthenticationPrincipal User user,
            HttpServletRequest httpRequest) {
        Student student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        StudentDetailResponse response = studentService.update(student.getPrn(), request, user.getId(), getIp(httpRequest));
        return ResponseEntity.ok(ApiResponse.success("Profile updated", response));
    }

    private String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
