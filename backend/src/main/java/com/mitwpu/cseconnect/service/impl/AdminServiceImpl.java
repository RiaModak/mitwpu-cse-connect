package com.mitwpu.cseconnect.service.impl;

import com.mitwpu.cseconnect.dto.request.ResetPasswordRequest;
import com.mitwpu.cseconnect.dto.response.AuditLogResponse;
import com.mitwpu.cseconnect.dto.response.DashboardStatsResponse;
import com.mitwpu.cseconnect.dto.response.PagedResponse;
import com.mitwpu.cseconnect.entity.*;
import com.mitwpu.cseconnect.exception.ResourceNotFoundException;
import com.mitwpu.cseconnect.repository.*;
import com.mitwpu.cseconnect.service.AdminService;
import com.mitwpu.cseconnect.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ClubRepository clubRepository;
    private final AchievementRepository achievementRepository;
    private final AuditLogRepository auditLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuditService auditService;

    @Override
    public DashboardStatsResponse getDashboardStats() {
        long totalStudents = studentRepository.countActive();
        long totalTeachers = teacherRepository.countActive();
        long totalClubs = clubRepository.countAll();
        long activeClubs = clubRepository.countActive();
        long totalAchievements = achievementRepository.countAll();
        long verifiedAchievements = achievementRepository.countVerified();
        long pendingVerifications = achievementRepository.countPending();

        Map<String, Long> achievementsByCategory = new HashMap<>();
        for (Object[] row : achievementRepository.countByCategory()) {
            achievementsByCategory.put(row[0].toString(), (Long) row[1]);
        }

        Map<String, Long> studentsByYear = new HashMap<>();
        for (int y = 1; y <= 4; y++) {
            studentsByYear.put("Year " + y, studentRepository.countByYear(y));
        }

        Map<String, Long> studentsByPanel = new HashMap<>();
        for (Student.Panel p : Student.Panel.values()) {
            studentsByPanel.put(p.name(), studentRepository.countByPanel(p));
        }

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        long auditEntriesToday = auditLogRepository.countSince(startOfDay);

        Page<AuditLog> recentLogs = auditLogRepository.findAllByOrderByCreatedAtDesc(
                org.springframework.data.domain.PageRequest.of(0, 8));
        List<AuditLogResponse> recentAuditLogs = recentLogs.getContent().stream()
                .map(this::toAuditLogResponse)
                .collect(Collectors.toList());

        return DashboardStatsResponse.builder()
                .totalStudents(totalStudents)
                .totalTeachers(totalTeachers)
                .totalClubs(totalClubs)
                .activeClubs(activeClubs)
                .totalAchievements(totalAchievements)
                .verifiedAchievements(verifiedAchievements)
                .pendingVerifications(pendingVerifications)
                .achievementsByCategory(achievementsByCategory)
                .studentsByYear(studentsByYear)
                .studentsByPanel(studentsByPanel)
                .recentAuditLogs(recentAuditLogs)
                .auditEntriesToday(auditEntriesToday)
                .build();
    }

    @Override
    public PagedResponse<AuditLogResponse> getAuditLogs(Pageable pageable) {
        Page<AuditLog> page = auditLogRepository.findAllByOrderByCreatedAtDesc(pageable);
        List<AuditLogResponse> content = page.getContent().stream()
                .map(this::toAuditLogResponse)
                .collect(Collectors.toList());
        return new PagedResponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }

    @Override
    @Transactional
    public void resetStudentPassword(String prn, ResetPasswordRequest request, Long actorUserId, String ip) {
        Student student = studentRepository.findByPrn(prn)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with PRN: " + prn));
        User user = student.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        auditService.log(actorUserId, null, null, AuditLog.AuditAction.PASSWORD_RESET,
                "Student", student.getId(), null, null,
                "Password reset for student: " + student.getFullName(), ip);
    }

    @Override
    @Transactional
    public void resetTeacherPassword(Long id, ResetPasswordRequest request, Long actorUserId, String ip) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with ID: " + id));
        User user = teacher.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        auditService.log(actorUserId, null, null, AuditLog.AuditAction.PASSWORD_RESET,
                "Teacher", teacher.getId(), null, null,
                "Password reset for teacher: " + teacher.getFullName(), ip);
    }

    private AuditLogResponse toAuditLogResponse(AuditLog log) {
        return AuditLogResponse.builder()
                .id(log.getId())
                .actorName(log.getActorName())
                .actorRole(log.getActorRole())
                .action(log.getAction().name())
                .entityName(log.getEntityName())
                .entityId(log.getEntityId())
                .description(log.getDescription())
                .ipAddress(log.getIpAddress())
                .oldValue(log.getOldValue())
                .newValue(log.getNewValue())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
