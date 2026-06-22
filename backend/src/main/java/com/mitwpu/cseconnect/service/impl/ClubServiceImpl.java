package com.mitwpu.cseconnect.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mitwpu.cseconnect.dto.request.*;
import com.mitwpu.cseconnect.dto.response.*;
import com.mitwpu.cseconnect.entity.*;
import com.mitwpu.cseconnect.exception.BadRequestException;
import com.mitwpu.cseconnect.exception.DuplicateResourceException;
import com.mitwpu.cseconnect.exception.ResourceNotFoundException;
import com.mitwpu.cseconnect.repository.*;
import com.mitwpu.cseconnect.service.AuditService;
import com.mitwpu.cseconnect.service.ClubService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClubServiceImpl implements ClubService {

    private final ClubRepository clubRepository;
    private final ClubMembershipRepository membershipRepository;
    private final ClubNoticeRepository noticeRepository;
    private final StudentRepository studentRepository;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    @Override
    public List<ClubSummaryResponse> getAll(String category, String search) {
        List<Club> clubs;
        if (category != null && !category.isEmpty()) {
            clubs = clubRepository.findByCategory(category);
        } else {
            clubs = clubRepository.findAllActive();
        }
        if (search != null && !search.isEmpty()) {
            String lowerSearch = search.toLowerCase();
            clubs = clubs.stream()
                    .filter(c -> c.getName().toLowerCase().contains(lowerSearch) ||
                            (c.getDescription() != null && c.getDescription().toLowerCase().contains(lowerSearch)))
                    .collect(Collectors.toList());
        }
        return clubs.stream().map(this::toSummary).collect(Collectors.toList());
    }

    @Override
    public ClubDetailResponse getById(Long id) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Club not found with ID: " + id));
        return toDetail(club);
    }

    @Override
    @Transactional
    public ClubSummaryResponse create(CreateClubRequest request, Long actorUserId, String ip) {
        Club club = new Club();
        club.setName(request.getName());
        club.setDescription(request.getDescription());
        club.setCategory(request.getCategory());
        club.setLogoUrl(request.getLogoUrl());
        club.setFoundedYear(request.getFoundedYear());
        club.setFacultyAdvisor(request.getFacultyAdvisor());
        club.setIsActive(true);
        club.setCreatedBy(actorUserId);
        club.setIsDeleted(false);
        club = clubRepository.save(club);

        auditService.log(actorUserId, null, null, AuditLog.AuditAction.CREATE,
                "Club", club.getId(), null, null,
                "Created club: " + club.getName(), ip);

        return toSummary(club);
    }

    @Override
    @Transactional
    public ClubSummaryResponse update(Long id, UpdateClubRequest request, Long actorUserId, String ip) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Club not found with ID: " + id));

        if (request.getName() != null) club.setName(request.getName());
        if (request.getDescription() != null) club.setDescription(request.getDescription());
        if (request.getCategory() != null) club.setCategory(request.getCategory());
        if (request.getLogoUrl() != null) club.setLogoUrl(request.getLogoUrl());
        if (request.getFoundedYear() != null) club.setFoundedYear(request.getFoundedYear());
        if (request.getFacultyAdvisor() != null) club.setFacultyAdvisor(request.getFacultyAdvisor());
        club.setUpdatedBy(actorUserId);
        club = clubRepository.save(club);

        auditService.log(actorUserId, null, null, AuditLog.AuditAction.UPDATE,
                "Club", club.getId(), null, null,
                "Updated club: " + club.getName(), ip);

        return toSummary(club);
    }

    @Override
    @Transactional
    public void deactivate(Long id, Long actorUserId, String ip) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Club not found with ID: " + id));
        club.setIsActive(false);
        club.setIsDeleted(true);
        club.setDeletedAt(LocalDateTime.now());
        clubRepository.save(club);

        auditService.log(actorUserId, null, null, AuditLog.AuditAction.DELETE,
                "Club", club.getId(), null, null,
                "Deactivated club: " + club.getName(), ip);
    }

    @Override
    @Transactional
    public MembershipResponse addMember(Long clubId, AddMemberRequest request, Long actorUserId, String ip) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("Club not found"));
        Student student = studentRepository.findByPrn(request.getStudentPrn())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with PRN: " + request.getStudentPrn()));

        if (membershipRepository.existsByStudentIdAndClubIdAndIsCurrentTrue(student.getId(), clubId)) {
            throw new DuplicateResourceException("Student is already a current member of this club");
        }

        ClubMembership membership = new ClubMembership();
        membership.setStudent(student);
        membership.setClub(club);
        membership.setRole(ClubMembership.ClubRole.valueOf(request.getRole()));
        membership.setStartYear(request.getStartYear());
        membership.setIsCurrent(true);
        membership.setJoinedVia(ClubMembership.JoinedVia.valueOf(request.getJoinedVia()));
        membership.setNotes(request.getNotes());
        membership.setCreatedBy(actorUserId);
        membership.setIsDeleted(false);
        membership = membershipRepository.save(membership);

        auditService.log(actorUserId, null, null, AuditLog.AuditAction.CREATE,
                "ClubMembership", membership.getId(), null, null,
                "Added " + student.getFullName() + " to " + club.getName() + " as " + request.getRole(), ip);

        return toMembershipResponse(membership);
    }

    @Override
    @Transactional
    public MembershipResponse updateMember(Long clubId, Long membershipId, UpdateMemberRequest request, Long actorUserId, String ip) {
        ClubMembership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found"));

        if (request.getRole() != null) membership.setRole(ClubMembership.ClubRole.valueOf(request.getRole()));
        if (request.getEndYear() != null) membership.setEndYear(request.getEndYear());
        if (request.getIsCurrent() != null) membership.setIsCurrent(request.getIsCurrent());
        if (request.getNotes() != null) membership.setNotes(request.getNotes());
        membership.setUpdatedBy(actorUserId);
        membership = membershipRepository.save(membership);

        auditService.log(actorUserId, null, null, AuditLog.AuditAction.UPDATE,
                "ClubMembership", membership.getId(), null, null,
                "Updated membership for " + membership.getStudent().getFullName(), ip);

        return toMembershipResponse(membership);
    }

    @Override
    @Transactional
    public void removeMember(Long clubId, Long membershipId, Long actorUserId, String ip) {
        ClubMembership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found"));
        membership.setIsCurrent(false);
        int currentYear = java.time.Year.now().getValue();
        membership.setEndYear(currentYear + "-" + (currentYear + 1));
        membership.setUpdatedBy(actorUserId);
        membershipRepository.save(membership);

        auditService.log(actorUserId, null, null, AuditLog.AuditAction.UPDATE,
                "ClubMembership", membership.getId(), null, null,
                "Removed " + membership.getStudent().getFullName() + " from club", ip);
    }

    @Override
    @Transactional
    public ClubNoticeResponse postNotice(Long clubId, PostClubNoticeRequest request, Long studentId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("Club not found"));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        if (!isClubHead(studentId, clubId)) {
            throw new BadRequestException("Only PRESIDENT or SECRETARY of this club can post notices");
        }

        ClubNotice notice = new ClubNotice();
        notice.setClub(club);
        notice.setPostedByStudent(student);
        notice.setTitle(request.getTitle());
        notice.setBody(request.getBody());
        notice.setIsDeleted(false);
        notice = noticeRepository.save(notice);

        return ClubNoticeResponse.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .body(notice.getBody())
                .postedByName(student.getFullName())
                .createdAt(notice.getCreatedAt())
                .build();
    }

    @Override
    public List<ClubNoticeResponse> getNotices(Long clubId) {
        return noticeRepository.findByClubIdAndIsDeletedFalseOrderByCreatedAtDesc(clubId).stream()
                .map(n -> ClubNoticeResponse.builder()
                        .id(n.getId())
                        .title(n.getTitle())
                        .body(n.getBody())
                        .postedByName(n.getPostedByStudent().getFullName())
                        .createdAt(n.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public boolean isClubHead(Long studentId, Long clubId) {
        return membershipRepository.findByStudentIdAndClubIdAndIsCurrentTrue(studentId, clubId)
                .map(m -> m.getRole() == ClubMembership.ClubRole.PRESIDENT || m.getRole() == ClubMembership.ClubRole.SECRETARY)
                .orElse(false);
    }

    @Override
    public boolean isCurrentMember(Long studentId, Long clubId) {
        return membershipRepository.existsByStudentIdAndClubIdAndIsCurrentTrue(studentId, clubId);
    }

    private ClubSummaryResponse toSummary(Club club) {
        List<ClubMembership> currentMembers = membershipRepository.findByClubIdAndIsCurrentTrueAndIsDeletedFalse(club.getId());
        String presidentName = null;
        String presidentPrn = null;
        for (ClubMembership m : currentMembers) {
            if (m.getRole() == ClubMembership.ClubRole.PRESIDENT) {
                presidentName = m.getStudent().getFullName();
                presidentPrn = m.getStudent().getPrn();
                break;
            }
        }

        return ClubSummaryResponse.builder()
                .id(club.getId())
                .name(club.getName())
                .description(club.getDescription())
                .category(club.getCategory())
                .logoUrl(club.getLogoUrl())
                .foundedYear(club.getFoundedYear())
                .facultyAdvisor(club.getFacultyAdvisor())
                .isActive(club.getIsActive())
                .currentMemberCount((long) currentMembers.size())
                .presidentName(presidentName)
                .presidentPrn(presidentPrn)
                .build();
    }

    private ClubDetailResponse toDetail(Club club) {
        List<ClubMembership> currentMembers = membershipRepository.findByClubIdAndIsCurrentTrueAndIsDeletedFalse(club.getId());
        List<ClubMembership> allMembers = membershipRepository.findByClubIdAndIsDeletedFalse(club.getId());
        List<ClubNotice> notices = noticeRepository.findByClubIdAndIsDeletedFalseOrderByCreatedAtDesc(club.getId());

        String presidentName = null;
        String presidentPrn = null;
        for (ClubMembership m : currentMembers) {
            if (m.getRole() == ClubMembership.ClubRole.PRESIDENT) {
                presidentName = m.getStudent().getFullName();
                presidentPrn = m.getStudent().getPrn();
                break;
            }
        }

        return ClubDetailResponse.builder()
                .id(club.getId())
                .name(club.getName())
                .description(club.getDescription())
                .category(club.getCategory())
                .logoUrl(club.getLogoUrl())
                .foundedYear(club.getFoundedYear())
                .facultyAdvisor(club.getFacultyAdvisor())
                .isActive(club.getIsActive())
                .currentMemberCount((long) currentMembers.size())
                .presidentName(presidentName)
                .presidentPrn(presidentPrn)
                .currentMembers(currentMembers.stream().map(this::toMemberDetail).collect(Collectors.toList()))
                .allTimeMembers(allMembers.stream().map(this::toMemberDetail).collect(Collectors.toList()))
                .recentNotices(notices.stream().map(n -> ClubNoticeResponse.builder()
                        .id(n.getId())
                        .title(n.getTitle())
                        .body(n.getBody())
                        .postedByName(n.getPostedByStudent().getFullName())
                        .createdAt(n.getCreatedAt())
                        .build()).collect(Collectors.toList()))
                .build();
    }

    private MemberDetailResponse toMemberDetail(ClubMembership m) {
        return MemberDetailResponse.builder()
                .membershipId(m.getId())
                .studentId(m.getStudent().getId())
                .prn(m.getStudent().getPrn())
                .studentName(m.getStudent().getFullName())
                .profilePhotoUrl(m.getStudent().getProfilePhotoUrl())
                .role(m.getRole().name())
                .startYear(m.getStartYear())
                .endYear(m.getEndYear())
                .isCurrent(m.getIsCurrent())
                .panel(m.getStudent().getPanel().name())
                .year(m.getStudent().getYear())
                .build();
    }

    private MembershipResponse toMembershipResponse(ClubMembership m) {
        return MembershipResponse.builder()
                .id(m.getId())
                .clubId(m.getClub().getId())
                .clubName(m.getClub().getName())
                .clubCategory(m.getClub().getCategory())
                .role(m.getRole().name())
                .startYear(m.getStartYear())
                .endYear(m.getEndYear())
                .isCurrent(m.getIsCurrent())
                .joinedVia(m.getJoinedVia() != null ? m.getJoinedVia().name() : null)
                .build();
    }
}
