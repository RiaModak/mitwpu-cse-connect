package com.mitwpu.cseconnect.service.impl;

import com.mitwpu.cseconnect.dto.request.CreateAnnouncementRequest;
import com.mitwpu.cseconnect.dto.response.AnnouncementResponse;
import com.mitwpu.cseconnect.entity.*;
import com.mitwpu.cseconnect.exception.ResourceNotFoundException;
import com.mitwpu.cseconnect.repository.*;
import com.mitwpu.cseconnect.service.AnnouncementService;
import com.mitwpu.cseconnect.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ClubRepository clubRepository;
    private final ClubMembershipRepository membershipRepository;
    private final AuditService auditService;

    @Override
    public List<AnnouncementResponse> getVisible(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Announcement> announcements;

        if (user.getRole() == User.Role.ADMIN) {
            announcements = announcementRepository.findAllActive();
        } else if (user.getRole() == User.Role.TEACHER) {
            announcements = announcementRepository.findVisibleForTeacher();
        } else {
            Student student = studentRepository.findByUserId(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
            Announcement.TargetPanel panel = Announcement.TargetPanel.valueOf(student.getPanel().name());
            List<ClubMembership> memberships = membershipRepository.findByStudentIdAndIsCurrentTrueAndIsDeletedFalse(student.getId());
            List<Long> clubIds = memberships.stream().map(m -> m.getClub().getId()).collect(Collectors.toList());
            if (clubIds.isEmpty()) clubIds.add(-1L);
            announcements = announcementRepository.findVisibleForStudent(panel, clubIds);
        }

        return announcements.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AnnouncementResponse create(CreateAnnouncementRequest request, Long actorUserId, String ip) {
        User user = userRepository.findById(actorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Announcement announcement = new Announcement();
        announcement.setTitle(request.getTitle());
        announcement.setBody(request.getBody());
        announcement.setPostedBy(user);
        announcement.setTargetAudience(Announcement.TargetAudience.valueOf(request.getTargetAudience()));
        announcement.setTargetPanel(Announcement.TargetPanel.valueOf(request.getTargetPanel()));
        announcement.setIsPinned(request.getIsPinned() != null ? request.getIsPinned() : false);
        announcement.setExpiresAt(request.getExpiresAt());
        announcement.setIsDeleted(false);

        if (request.getTargetClubId() != null) {
            Club club = clubRepository.findById(request.getTargetClubId())
                    .orElseThrow(() -> new ResourceNotFoundException("Club not found"));
            announcement.setTargetClub(club);
        }

        announcement = announcementRepository.save(announcement);

        auditService.log(actorUserId, null, null, AuditLog.AuditAction.CREATE,
                "Announcement", announcement.getId(), null, null,
                "Created announcement: " + announcement.getTitle(), ip);

        return toResponse(announcement);
    }

    @Override
    @Transactional
    public void delete(Long id, Long actorUserId, String ip) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement not found"));
        announcement.setIsDeleted(true);
        announcement.setDeletedAt(LocalDateTime.now());
        announcementRepository.save(announcement);

        auditService.log(actorUserId, null, null, AuditLog.AuditAction.DELETE,
                "Announcement", announcement.getId(), null, null,
                "Deleted announcement: " + announcement.getTitle(), ip);
    }

    private AnnouncementResponse toResponse(Announcement a) {
        String postedByName = "Admin";
        if (a.getPostedBy().getRole() == User.Role.TEACHER) {
            postedByName = teacherRepository.findByUserId(a.getPostedBy().getId())
                    .map(Teacher::getFullName).orElse(a.getPostedBy().getEmail());
        }
        return AnnouncementResponse.builder()
                .id(a.getId())
                .title(a.getTitle())
                .body(a.getBody())
                .postedByName(postedByName)
                .postedByRole(a.getPostedBy().getRole().name())
                .targetAudience(a.getTargetAudience().name())
                .targetClubName(a.getTargetClub() != null ? a.getTargetClub().getName() : null)
                .targetPanel(a.getTargetPanel() != null ? a.getTargetPanel().name() : null)
                .isPinned(a.getIsPinned())
                .expiresAt(a.getExpiresAt())
                .createdAt(a.getCreatedAt())
                .build();
    }
}
