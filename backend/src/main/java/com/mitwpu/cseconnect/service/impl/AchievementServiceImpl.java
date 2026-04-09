package com.mitwpu.cseconnect.service.impl;

import com.mitwpu.cseconnect.dto.request.SubmitAchievementRequest;
import com.mitwpu.cseconnect.dto.request.VerifyAchievementRequest;
import com.mitwpu.cseconnect.dto.response.AchievementResponse;
import com.mitwpu.cseconnect.dto.response.PagedResponse;
import com.mitwpu.cseconnect.entity.*;
import com.mitwpu.cseconnect.exception.BadRequestException;
import com.mitwpu.cseconnect.exception.ResourceNotFoundException;
import com.mitwpu.cseconnect.repository.*;
import com.mitwpu.cseconnect.service.AchievementService;
import com.mitwpu.cseconnect.service.AuditService;
import com.mitwpu.cseconnect.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AchievementServiceImpl implements AchievementService {

    private final AchievementRepository achievementRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final AuditService auditService;
    private final FileStorageService fileStorageService;

    @Override
    public PagedResponse<AchievementResponse> getAll(String category, String status, Pageable pageable) {
        Achievement.AchievementCategory catEnum = null;
        Achievement.AchievementStatus statusEnum = null;
        if (category != null && !category.isEmpty()) catEnum = Achievement.AchievementCategory.valueOf(category);
        if (status != null && !status.isEmpty()) statusEnum = Achievement.AchievementStatus.valueOf(status);

        Page<Achievement> page = achievementRepository.findAllWithFilters(catEnum, statusEnum, pageable);
        List<AchievementResponse> content = page.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return new PagedResponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }

    @Override
    public List<AchievementResponse> getByStudent(String prn) {
        Student student = studentRepository.findByPrn(prn)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with PRN: " + prn));
        return achievementRepository.findByStudentIdAndIsDeletedFalse(student.getId()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AchievementResponse getById(Long id) {
        Achievement achievement = achievementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Achievement not found with ID: " + id));
        return toResponse(achievement);
    }

    @Override
    @Transactional
    public AchievementResponse submit(SubmitAchievementRequest request, MultipartFile proofFile, Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        Achievement achievement = new Achievement();
        achievement.setStudent(student);
        achievement.setTitle(request.getTitle());
        achievement.setCategory(Achievement.AchievementCategory.valueOf(request.getCategory()));
        achievement.setDescription(request.getDescription());
        achievement.setDateOfAchievement(request.getDateOfAchievement());
        achievement.setProofExternalUrl(request.getProofExternalUrl());
        achievement.setIssuingOrganization(request.getIssuingOrganization());
        achievement.setStatus(Achievement.AchievementStatus.PENDING);
        achievement.setIsDeleted(false);

        if (proofFile != null && !proofFile.isEmpty()) {
            String fileUrl = fileStorageService.store(proofFile, "achievements");
            achievement.setProofFileUrl(fileUrl);
        }

        achievement = achievementRepository.save(achievement);

        return toResponse(achievement);
    }

    @Override
    @Transactional
    public AchievementResponse verify(Long id, VerifyAchievementRequest request, Long actorUserId, String ip) {
        Achievement achievement = achievementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Achievement not found with ID: " + id));

        if (achievement.getStatus() != Achievement.AchievementStatus.PENDING) {
            throw new BadRequestException("Only PENDING achievements can be verified or rejected");
        }

        Achievement.AchievementStatus newStatus = Achievement.AchievementStatus.valueOf(request.getStatus());

        if (newStatus == Achievement.AchievementStatus.VERIFIED) {
            achievement.setStatus(Achievement.AchievementStatus.VERIFIED);
            achievement.setVerifiedBy(actorUserId);
            achievement.setVerifiedAt(LocalDateTime.now());

            auditService.log(actorUserId, null, null, AuditLog.AuditAction.VERIFY,
                    "Achievement", achievement.getId(), null, null,
                    "Verified achievement: " + achievement.getTitle(), ip);
        } else if (newStatus == Achievement.AchievementStatus.REJECTED) {
            if (request.getRejectionReason() == null || request.getRejectionReason().isEmpty()) {
                throw new BadRequestException("Rejection reason is required");
            }
            achievement.setStatus(Achievement.AchievementStatus.REJECTED);
            achievement.setRejectionReason(request.getRejectionReason());

            auditService.log(actorUserId, null, null, AuditLog.AuditAction.REJECT,
                    "Achievement", achievement.getId(), null, null,
                    "Rejected achievement: " + achievement.getTitle(), ip);
        }

        achievement = achievementRepository.save(achievement);
        return toResponse(achievement);
    }

    @Override
    @Transactional
    public void delete(Long id, Long actorUserId, String ip) {
        Achievement achievement = achievementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Achievement not found with ID: " + id));
        achievement.setIsDeleted(true);
        achievement.setDeletedAt(LocalDateTime.now());
        achievementRepository.save(achievement);

        auditService.log(actorUserId, null, null, AuditLog.AuditAction.DELETE,
                "Achievement", achievement.getId(), null, null,
                "Deleted achievement: " + achievement.getTitle(), ip);
    }

    private AchievementResponse toResponse(Achievement a) {
        String verifiedByName = null;
        if (a.getVerifiedBy() != null) {
            User verifier = userRepository.findById(a.getVerifiedBy()).orElse(null);
            if (verifier != null) {
                if (verifier.getRole() == User.Role.TEACHER) {
                    verifiedByName = teacherRepository.findByUserId(verifier.getId())
                            .map(Teacher::getFullName).orElse(verifier.getEmail());
                } else {
                    verifiedByName = "Admin";
                }
            }
        }
        return AchievementResponse.builder()
                .id(a.getId())
                .title(a.getTitle())
                .category(a.getCategory().name())
                .description(a.getDescription())
                .dateOfAchievement(a.getDateOfAchievement())
                .proofFileUrl(a.getProofFileUrl())
                .proofExternalUrl(a.getProofExternalUrl())
                .issuingOrganization(a.getIssuingOrganization())
                .status(a.getStatus().name())
                .verifiedByName(verifiedByName)
                .verifiedAt(a.getVerifiedAt())
                .rejectionReason(a.getRejectionReason())
                .studentName(a.getStudent().getFullName())
                .studentPrn(a.getStudent().getPrn())
                .studentId(a.getStudent().getId())
                .createdAt(a.getCreatedAt())
                .build();
    }
}
