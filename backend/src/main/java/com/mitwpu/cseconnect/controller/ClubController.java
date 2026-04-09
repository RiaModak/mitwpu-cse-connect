package com.mitwpu.cseconnect.controller;

import com.mitwpu.cseconnect.dto.request.AddMemberRequest;
import com.mitwpu.cseconnect.dto.request.PostClubNoticeRequest;
import com.mitwpu.cseconnect.dto.request.CreateClubRequest;
import com.mitwpu.cseconnect.dto.request.UpdateClubRequest;
import com.mitwpu.cseconnect.dto.request.UpdateMemberRequest;
import com.mitwpu.cseconnect.dto.response.*;
import com.mitwpu.cseconnect.entity.Student;
import com.mitwpu.cseconnect.entity.User;
import com.mitwpu.cseconnect.exception.ResourceNotFoundException;
import com.mitwpu.cseconnect.repository.StudentRepository;
import com.mitwpu.cseconnect.service.ClubService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;
    private final StudentRepository studentRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClubSummaryResponse>>> getAll(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {
        List<ClubSummaryResponse> response = clubService.getAll(category, search);
        return ResponseEntity.ok(ApiResponse.success("Clubs retrieved", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClubDetailResponse>> getById(@PathVariable Long id) {
        ClubDetailResponse response = clubService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Club retrieved", response));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ClubSummaryResponse>> create(
            @Valid @RequestBody CreateClubRequest request,
            @AuthenticationPrincipal User user,
            HttpServletRequest httpRequest) {
        ClubSummaryResponse response = clubService.create(request, user.getId(), getIp(httpRequest));
        return ResponseEntity.ok(ApiResponse.success("Club created", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ClubSummaryResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateClubRequest request,
            @AuthenticationPrincipal User user,
            HttpServletRequest httpRequest) {
        ClubSummaryResponse response = clubService.update(id, request, user.getId(), getIp(httpRequest));
        return ResponseEntity.ok(ApiResponse.success("Club updated", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivate(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            HttpServletRequest httpRequest) {
        clubService.deactivate(id, user.getId(), getIp(httpRequest));
        return ResponseEntity.ok(ApiResponse.success("Club deactivated", null));
    }

    @PostMapping("/{id}/members")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MembershipResponse>> addMember(
            @PathVariable Long id,
            @Valid @RequestBody AddMemberRequest request,
            @AuthenticationPrincipal User user,
            HttpServletRequest httpRequest) {
        MembershipResponse response = clubService.addMember(id, request, user.getId(), getIp(httpRequest));
        return ResponseEntity.ok(ApiResponse.success("Member added", response));
    }

    @PostMapping("/{id}/join")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<MembershipResponse>> joinClub(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            HttpServletRequest httpRequest) {
        Student student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        AddMemberRequest request = new AddMemberRequest();
        request.setStudentPrn(student.getPrn());
        request.setRole("MEMBER");
        int currentYear = java.time.Year.now().getValue();
        request.setStartYear(currentYear + "-" + (currentYear + 1));
        request.setJoinedVia("APPLICATION");
        MembershipResponse response = clubService.addMember(id, request, user.getId(), getIp(httpRequest));
        return ResponseEntity.ok(ApiResponse.success("Successfully joined club", response));
    }

    @PutMapping("/{id}/members/{membershipId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MembershipResponse>> updateMember(
            @PathVariable Long id,
            @PathVariable Long membershipId,
            @Valid @RequestBody UpdateMemberRequest request,
            @AuthenticationPrincipal User user,
            HttpServletRequest httpRequest) {
        MembershipResponse response = clubService.updateMember(id, membershipId, request, user.getId(), getIp(httpRequest));
        return ResponseEntity.ok(ApiResponse.success("Member updated", response));
    }

    @DeleteMapping("/{id}/members/{membershipId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @PathVariable Long id,
            @PathVariable Long membershipId,
            @AuthenticationPrincipal User user,
            HttpServletRequest httpRequest) {
        clubService.removeMember(id, membershipId, user.getId(), getIp(httpRequest));
        return ResponseEntity.ok(ApiResponse.success("Member removed", null));
    }

    @PostMapping("/{id}/notices")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<ClubNoticeResponse>> postNotice(
            @PathVariable Long id,
            @Valid @RequestBody PostClubNoticeRequest request,
            @AuthenticationPrincipal User user) {
        Student student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        ClubNoticeResponse response = clubService.postNotice(id, request, student.getId());
        return ResponseEntity.ok(ApiResponse.success("Notice posted", response));
    }

    @GetMapping("/{id}/notices")
    public ResponseEntity<ApiResponse<List<ClubNoticeResponse>>> getNotices(@PathVariable Long id) {
        List<ClubNoticeResponse> response = clubService.getNotices(id);
        return ResponseEntity.ok(ApiResponse.success("Notices retrieved", response));
    }

    private String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
