package com.mitwpu.cseconnect.controller;

import com.mitwpu.cseconnect.dto.request.AddMemberRequest;
import com.mitwpu.cseconnect.dto.request.PostClubNoticeRequest;
import com.mitwpu.cseconnect.dto.request.CreateClubRequest;
import com.mitwpu.cseconnect.dto.request.UpdateClubRequest;
import com.mitwpu.cseconnect.dto.request.UpdateMemberRequest;
import com.mitwpu.cseconnect.dto.response.*;
import com.mitwpu.cseconnect.entity.ClubJoinRequest;
import com.mitwpu.cseconnect.entity.Student;
import com.mitwpu.cseconnect.entity.User;
import com.mitwpu.cseconnect.exception.ResourceNotFoundException;
import com.mitwpu.cseconnect.repository.ClubJoinRequestRepository;
import com.mitwpu.cseconnect.repository.StudentRepository;
import com.mitwpu.cseconnect.repository.ClubRepository;
import com.mitwpu.cseconnect.entity.Club;
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
    private final ClubJoinRequestRepository joinRequestRepository;
    private final ClubRepository clubRepository;

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
    public ResponseEntity<ApiResponse<ClubJoinRequestResponse>> requestJoinClub(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        Student student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Club not found"));

        // Check if already a member
        if (clubService.isCurrentMember(student.getId(), id)) {
            throw new IllegalStateException("You are already a member of this club");
        }

        // Check if already has a pending request
        if (joinRequestRepository.existsByStudentIdAndClubIdAndStatusAndIsDeletedFalse(
                student.getId(), id, ClubJoinRequest.JoinRequestStatus.PENDING)) {
            throw new IllegalStateException("You already have a pending join request for this club");
        }

        ClubJoinRequest joinRequest = new ClubJoinRequest();
        joinRequest.setStudent(student);
        joinRequest.setClub(club);
        joinRequest.setStatus(ClubJoinRequest.JoinRequestStatus.PENDING);
        joinRequest.setIsDeleted(false);
        joinRequest = joinRequestRepository.save(joinRequest);

        ClubJoinRequestResponse response = toJoinRequestResponse(joinRequest);
        return ResponseEntity.ok(ApiResponse.success("Join request submitted. Awaiting teacher approval.", response));
    }

    @GetMapping("/join-requests/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<ClubJoinRequestResponse>>> getMyJoinRequests(
            @AuthenticationPrincipal User user) {
        Student student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        List<ClubJoinRequest> requests = joinRequestRepository.findByStudentIdAndIsDeletedFalse(student.getId());
        List<ClubJoinRequestResponse> response = requests.stream()
                .map(this::toJoinRequestResponse)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("My join requests retrieved", response));
    }

    @GetMapping("/join-requests/pending")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<ClubJoinRequestResponse>>> getPendingJoinRequests() {
        List<ClubJoinRequest> requests = joinRequestRepository.findByStatusAndIsDeletedFalse(
                ClubJoinRequest.JoinRequestStatus.PENDING);
        List<ClubJoinRequestResponse> response = requests.stream()
                .map(this::toJoinRequestResponse)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Pending join requests retrieved", response));
    }

    @PutMapping("/join-requests/{requestId}/approve")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<MembershipResponse>> approveJoinRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal User user,
            HttpServletRequest httpRequest) {
        ClubJoinRequest joinRequest = joinRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Join request not found"));

        if (joinRequest.getStatus() != ClubJoinRequest.JoinRequestStatus.PENDING) {
            throw new IllegalStateException("This request has already been " + joinRequest.getStatus().name().toLowerCase());
        }

        // Create the membership
        AddMemberRequest addRequest = new AddMemberRequest();
        addRequest.setStudentPrn(joinRequest.getStudent().getPrn());
        addRequest.setRole("MEMBER");
        int currentYear = java.time.Year.now().getValue();
        addRequest.setStartYear(currentYear + "-" + (currentYear + 1));
        addRequest.setJoinedVia("APPLICATION");
        MembershipResponse membershipResponse = clubService.addMember(
                joinRequest.getClub().getId(), addRequest, user.getId(), getIp(httpRequest));

        // Update join request status
        joinRequest.setStatus(ClubJoinRequest.JoinRequestStatus.APPROVED);
        joinRequest.setReviewedBy(user.getId());
        joinRequest.setReviewedAt(java.time.LocalDateTime.now());
        joinRequestRepository.save(joinRequest);

        return ResponseEntity.ok(ApiResponse.success("Join request approved. Student added to club.", membershipResponse));
    }

    @PutMapping("/join-requests/{requestId}/reject")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ClubJoinRequestResponse>> rejectJoinRequest(
            @PathVariable Long requestId,
            @RequestParam(required = false) String reason,
            @AuthenticationPrincipal User user) {
        ClubJoinRequest joinRequest = joinRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Join request not found"));

        if (joinRequest.getStatus() != ClubJoinRequest.JoinRequestStatus.PENDING) {
            throw new IllegalStateException("This request has already been " + joinRequest.getStatus().name().toLowerCase());
        }

        joinRequest.setStatus(ClubJoinRequest.JoinRequestStatus.REJECTED);
        joinRequest.setReviewedBy(user.getId());
        joinRequest.setReviewedAt(java.time.LocalDateTime.now());
        joinRequest.setRejectionReason(reason);
        joinRequest = joinRequestRepository.save(joinRequest);

        return ResponseEntity.ok(ApiResponse.success("Join request rejected.", toJoinRequestResponse(joinRequest)));
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

    private ClubJoinRequestResponse toJoinRequestResponse(ClubJoinRequest r) {
        return ClubJoinRequestResponse.builder()
                .id(r.getId())
                .clubId(r.getClub().getId())
                .clubName(r.getClub().getName())
                .clubCategory(r.getClub().getCategory())
                .studentId(r.getStudent().getId())
                .studentPrn(r.getStudent().getPrn())
                .studentName(r.getStudent().getFullName())
                .studentPanel(r.getStudent().getPanel().name())
                .studentYear(r.getStudent().getYear())
                .status(r.getStatus().name())
                .rejectionReason(r.getRejectionReason())
                .createdAt(r.getCreatedAt())
                .reviewedAt(r.getReviewedAt())
                .build();
    }
}
