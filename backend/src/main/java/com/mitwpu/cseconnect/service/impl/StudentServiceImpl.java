package com.mitwpu.cseconnect.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mitwpu.cseconnect.dto.request.AddAcademicRecordRequest;
import com.mitwpu.cseconnect.dto.request.CreateStudentRequest;
import com.mitwpu.cseconnect.dto.request.UpdateStudentRequest;
import com.mitwpu.cseconnect.dto.response.*;
import com.mitwpu.cseconnect.entity.*;
import com.mitwpu.cseconnect.exception.BadRequestException;
import com.mitwpu.cseconnect.exception.DuplicateResourceException;
import com.mitwpu.cseconnect.exception.ResourceNotFoundException;
import com.mitwpu.cseconnect.repository.*;
import com.mitwpu.cseconnect.service.AuditService;
import com.mitwpu.cseconnect.service.StudentService;
import com.opencsv.CSVReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final ClubMembershipRepository membershipRepository;
    private final AchievementRepository achievementRepository;
    private final AcademicRecordRepository academicRecordRepository;
    private final TeacherRepository teacherRepository;
    private final TeacherPanelAssignmentRepository panelAssignmentRepository;
    private final AnnouncementRepository announcementRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    @Override
    public PagedResponse<StudentSummaryResponse> getAll(String panel, Integer year, String search, Pageable pageable) {
        Student.Panel panelEnum = panel != null && !panel.isEmpty() ? Student.Panel.valueOf(panel) : null;
        Page<Student> page = studentRepository.searchStudents(null, search, panelEnum, year, pageable);
        List<StudentSummaryResponse> content = page.getContent().stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
        return new PagedResponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }

    @Override
    public StudentDetailResponse getByPrn(String prn) {
        Student student = studentRepository.findByPrn(prn)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with PRN: " + prn));
        return toDetail(student);
    }

    @Override
    @Transactional
    public StudentDetailResponse create(CreateStudentRequest request, Long actorUserId, String ip) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + request.getEmail());
        }
        if (studentRepository.existsByPrn(request.getPrn())) {
            throw new DuplicateResourceException("PRN already exists: " + request.getPrn());
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.STUDENT);
        user.setIsActive(true);
        user.setIsDeleted(false);
        user = userRepository.save(user);

        Student student = new Student();
        student.setUser(user);
        student.setPrn(request.getPrn());
        student.setFullName(request.getFullName());
        student.setPanel(Student.Panel.valueOf(request.getPanel()));
        student.setYear(request.getYear());
        student.setCgpa(request.getCgpa() != null ? request.getCgpa() : BigDecimal.ZERO);
        student.setAttendancePercent(request.getAttendancePercent() != null ? request.getAttendancePercent() : BigDecimal.ZERO);
        student.setPhone(request.getPhone());
        student.setGithubUrl(request.getGithubUrl());
        student.setLinkedinUrl(request.getLinkedinUrl());
        student.setInternshipCompany(request.getInternshipCompany());
        student.setInternshipRole(request.getInternshipRole());
        student.setInternshipStart(request.getInternshipStart());
        student.setInternshipEnd(request.getInternshipEnd());
        student.setSkills(request.getSkills());
        student.setBio(request.getBio());
        student.setCreatedBy(actorUserId);
        student.setIsDeleted(false);
        student = studentRepository.save(student);

        auditService.log(actorUserId, null, null, AuditLog.AuditAction.CREATE,
                "Student", student.getId(), null, toJson(student),
                "Created student: " + student.getFullName() + " (PRN: " + student.getPrn() + ")", ip);

        return toDetail(student);
    }

    @Override
    @Transactional
    public StudentDetailResponse update(String prn, UpdateStudentRequest request, Long actorUserId, String ip) {
        Student student = studentRepository.findByPrn(prn)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with PRN: " + prn));

        String oldJson = toJson(student);

        if (request.getFullName() != null) student.setFullName(request.getFullName());
        if (request.getPanel() != null) student.setPanel(Student.Panel.valueOf(request.getPanel()));
        if (request.getYear() != null) student.setYear(request.getYear());
        if (request.getCgpa() != null) student.setCgpa(request.getCgpa());
        if (request.getAttendancePercent() != null) student.setAttendancePercent(request.getAttendancePercent());
        if (request.getPhone() != null) student.setPhone(request.getPhone());
        if (request.getGithubUrl() != null) student.setGithubUrl(request.getGithubUrl());
        if (request.getLinkedinUrl() != null) student.setLinkedinUrl(request.getLinkedinUrl());
        if (request.getInternshipCompany() != null) student.setInternshipCompany(request.getInternshipCompany());
        if (request.getInternshipRole() != null) student.setInternshipRole(request.getInternshipRole());
        if (request.getInternshipStart() != null) student.setInternshipStart(request.getInternshipStart());
        if (request.getInternshipEnd() != null) student.setInternshipEnd(request.getInternshipEnd());
        if (request.getSkills() != null) student.setSkills(request.getSkills());
        if (request.getBio() != null) student.setBio(request.getBio());
        if (request.getProfilePhotoUrl() != null) student.setProfilePhotoUrl(request.getProfilePhotoUrl());
        student.setUpdatedBy(actorUserId);
        student = studentRepository.save(student);

        auditService.log(actorUserId, null, null, AuditLog.AuditAction.UPDATE,
                "Student", student.getId(), oldJson, toJson(student),
                "Updated student: " + student.getFullName(), ip);

        return toDetail(student);
    }

    @Override
    @Transactional
    public void delete(String prn, Long actorUserId, String ip) {
        Student student = studentRepository.findByPrn(prn)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with PRN: " + prn));
        student.setIsDeleted(true);
        student.setDeletedAt(LocalDateTime.now());
        studentRepository.save(student);

        User user = student.getUser();
        user.setIsDeleted(true);
        user.setDeletedAt(LocalDateTime.now());
        user.setIsActive(false);
        userRepository.save(user);

        auditService.log(actorUserId, null, null, AuditLog.AuditAction.DELETE,
                "Student", student.getId(), null, null,
                "Deleted student: " + student.getFullName() + " (PRN: " + prn + ")", ip);
    }

    @Override
    @Transactional
    public void resetPassword(String prn, String newPassword, Long actorUserId, String ip) {
        Student student = studentRepository.findByPrn(prn)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with PRN: " + prn));
        User user = student.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        auditService.log(actorUserId, null, null, AuditLog.AuditAction.PASSWORD_RESET,
                "Student", student.getId(), null, null,
                "Password reset for student: " + student.getFullName(), ip);
    }

    @Override
    public List<MembershipResponse> getClubHistory(String prn) {
        Student student = studentRepository.findByPrn(prn)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with PRN: " + prn));
        List<ClubMembership> memberships = membershipRepository.findByStudentIdAndIsDeletedFalse(student.getId());
        return memberships.stream()
                .sorted((a, b) -> b.getStartYear().compareTo(a.getStartYear()))
                .map(this::toMembershipResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AchievementResponse> getAchievements(String prn) {
        Student student = studentRepository.findByPrn(prn)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with PRN: " + prn));
        return achievementRepository.findByStudentIdAndIsDeletedFalse(student.getId()).stream()
                .map(this::toAchievementResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TimelineItemResponse> getTimeline(String prn) {
        Student student = studentRepository.findByPrn(prn)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with PRN: " + prn));
        List<TimelineItemResponse> timeline = new ArrayList<>();

        List<ClubMembership> memberships = membershipRepository.findByStudentIdAndIsDeletedFalse(student.getId());
        for (ClubMembership m : memberships) {
            Map<String, String> metadata = new HashMap<>();
            metadata.put("clubName", m.getClub().getName());
            metadata.put("role", m.getRole().name());
            metadata.put("startYear", m.getStartYear());
            if (m.getEndYear() != null) metadata.put("endYear", m.getEndYear());

            String type = m.getIsCurrent() ? "CLUB_JOIN" : "CLUB_LEAVE";
            if (m.getRole() != ClubMembership.ClubRole.MEMBER) type = "CLUB_ROLE_CHANGE";

            timeline.add(TimelineItemResponse.builder()
                    .type(type)
                    .date(LocalDate.of(Integer.parseInt(m.getStartYear().substring(0, 4)), 7, 1))
                    .title(m.getClub().getName())
                    .subtitle(m.getRole().name() + " (" + m.getStartYear() + ")")
                    .category(m.getClub().getCategory())
                    .status(m.getIsCurrent() ? "ACTIVE" : "PAST")
                    .metadata(metadata)
                    .build());
        }

        List<Achievement> achievements = achievementRepository.findByStudentIdAndIsDeletedFalse(student.getId());
        for (Achievement a : achievements) {
            Map<String, String> metadata = new HashMap<>();
            metadata.put("organization", a.getIssuingOrganization());

            timeline.add(TimelineItemResponse.builder()
                    .type("ACHIEVEMENT")
                    .date(a.getDateOfAchievement())
                    .title(a.getTitle())
                    .subtitle(a.getIssuingOrganization())
                    .category(a.getCategory().name())
                    .status(a.getStatus().name())
                    .metadata(metadata)
                    .build());
        }

        timeline.sort((a, b) -> b.getDate().compareTo(a.getDate()));
        return timeline;
    }

    @Override
    @Transactional
    public BulkImportResultResponse bulkImport(MultipartFile csv, Long actorUserId, String ip) {
        List<String> errors = new ArrayList<>();
        int successCount = 0;
        int totalProcessed = 0;

        try (CSVReader reader = new CSVReader(new InputStreamReader(csv.getInputStream()))) {
            String[] header = reader.readNext();
            String[] line;
            while ((line = reader.readNext()) != null) {
                totalProcessed++;
                try {
                    if (line.length < 6) {
                        errors.add("Row " + totalProcessed + ": Insufficient columns");
                        continue;
                    }
                    String prn = line[0].trim();
                    String fullName = line[1].trim();
                    String panel = line[2].trim();
                    int year = Integer.parseInt(line[3].trim());
                    String email = line[4].trim();
                    String password = line[5].trim();

                    CreateStudentRequest request = new CreateStudentRequest();
                    request.setPrn(prn);
                    request.setFullName(fullName);
                    request.setPanel(panel);
                    request.setYear(year);
                    request.setEmail(email);
                    request.setPassword(password);
                    if (line.length > 6) request.setCgpa(new BigDecimal(line[6].trim()));
                    if (line.length > 7) request.setAttendancePercent(new BigDecimal(line[7].trim()));

                    create(request, actorUserId, ip);
                    successCount++;
                } catch (Exception e) {
                    errors.add("Row " + totalProcessed + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            throw new BadRequestException("Failed to parse CSV file: " + e.getMessage());
        }

        auditService.log(actorUserId, null, null, AuditLog.AuditAction.BULK_IMPORT,
                "Student", null, null, null,
                "Bulk import: " + successCount + " created, " + errors.size() + " failed", ip);

        return BulkImportResultResponse.builder()
                .totalProcessed(totalProcessed)
                .successCount(successCount)
                .failureCount(errors.size())
                .errors(errors)
                .build();
    }

    @Override
    @Transactional
    public AcademicRecordResponse addAcademicRecord(String prn, AddAcademicRecordRequest request, Long actorUserId, String ip) {
        Student student = studentRepository.findByPrn(prn)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with PRN: " + prn));

        AcademicRecord record = new AcademicRecord();
        record.setStudent(student);
        record.setAcademicYear(request.getAcademicYear());
        record.setSemester(request.getSemester());
        record.setSgpa(request.getSgpa());
        record.setCgpa(request.getCgpa());
        record.setAttendancePercent(request.getAttendancePercent());
        record.setBacklogs(request.getBacklogs() != null ? request.getBacklogs() : 0);
        record.setRemarks(request.getRemarks());
        record.setRecordedBy(actorUserId);
        record = academicRecordRepository.save(record);

        auditService.log(actorUserId, null, null, AuditLog.AuditAction.CREATE,
                "AcademicRecord", record.getId(), null, null,
                "Added academic record for " + student.getFullName() + " Sem " + request.getSemester(), ip);

        return toAcademicRecordResponse(record);
    }

    @Override
    public DashboardStatsResponse getStudentDashboard(Long userId) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        List<ClubMembership> currentMemberships = membershipRepository.findByStudentIdAndIsCurrentTrueAndIsDeletedFalse(student.getId());
        List<Achievement> achievements = achievementRepository.findByStudentIdAndIsDeletedFalse(student.getId());
        List<AcademicRecord> records = academicRecordRepository.findByStudentIdOrderByAcademicYearDescSemesterDesc(student.getId());

        Announcement.TargetPanel targetPanel = Announcement.TargetPanel.valueOf(student.getPanel().name());
        List<Long> clubIds = currentMemberships.stream()
                .map(m -> m.getClub().getId())
                .collect(Collectors.toList());
        if (clubIds.isEmpty()) clubIds.add(-1L);
        List<Announcement> announcements = announcementRepository.findVisibleForStudent(targetPanel, clubIds);

        return DashboardStatsResponse.builder()
                .cgpa(student.getCgpa())
                .attendancePercent(student.getAttendancePercent())
                .activeClubsCount((long) currentMemberships.size())
                .achievementsCount((long) achievements.size())
                .verifiedAchievementsCount(achievements.stream()
                        .filter(a -> a.getStatus() == Achievement.AchievementStatus.VERIFIED).count())
                .currentClubs(currentMemberships.stream().map(this::toMembershipResponse).collect(Collectors.toList()))
                .recentAnnouncements(announcements.stream().limit(4).map(this::toAnnouncementResponse).collect(Collectors.toList()))
                .academicRecords(records.stream().map(this::toAcademicRecordResponse).collect(Collectors.toList()))
                .build();
    }

    private StudentSummaryResponse toSummary(Student student) {
        List<ClubMembership> currentMemberships = membershipRepository.findByStudentIdAndIsCurrentTrueAndIsDeletedFalse(student.getId());
        String activeClubName = null;
        String activeClubRole = null;
        if (!currentMemberships.isEmpty()) {
            ClubMembership first = currentMemberships.get(0);
            activeClubName = first.getClub().getName();
            activeClubRole = first.getRole().name();
        }

        return StudentSummaryResponse.builder()
                .id(student.getId())
                .prn(student.getPrn())
                .fullName(student.getFullName())
                .panel(student.getPanel().name())
                .year(student.getYear())
                .cgpa(student.getCgpa())
                .attendancePercent(student.getAttendancePercent())
                .activeClubName(activeClubName)
                .activeClubRole(activeClubRole)
                .achievementsCount((long) achievementRepository.findByStudentIdAndIsDeletedFalse(student.getId()).size())
                .profilePhotoUrl(student.getProfilePhotoUrl())
                .build();
    }

    private StudentDetailResponse toDetail(Student student) {
        List<ClubMembership> allMemberships = membershipRepository.findByStudentIdAndIsDeletedFalse(student.getId());
        List<MembershipResponse> currentClubs = allMemberships.stream()
                .filter(ClubMembership::getIsCurrent)
                .map(this::toMembershipResponse)
                .collect(Collectors.toList());
        List<MembershipResponse> clubHistory = allMemberships.stream()
                .sorted((a, b) -> b.getStartYear().compareTo(a.getStartYear()))
                .map(this::toMembershipResponse)
                .collect(Collectors.toList());

        List<AchievementResponse> achievements = achievementRepository.findByStudentIdAndIsDeletedFalse(student.getId())
                .stream().map(this::toAchievementResponse).collect(Collectors.toList());

        List<AcademicRecordResponse> records = academicRecordRepository
                .findByStudentIdOrderByAcademicYearDescSemesterDesc(student.getId())
                .stream().map(this::toAcademicRecordResponse).collect(Collectors.toList());

        String panelTeacherName = null;
        String panelTeacherEmail = null;
        Optional<TeacherPanelAssignment> assignment = panelAssignmentRepository.findByPanelAndIsCurrentTrue(student.getPanel());
        if (assignment.isPresent()) {
            Teacher teacher = assignment.get().getTeacher();
            panelTeacherName = teacher.getFullName();
            panelTeacherEmail = teacher.getUser().getEmail();
        }

        return StudentDetailResponse.builder()
                .id(student.getId())
                .prn(student.getPrn())
                .fullName(student.getFullName())
                .panel(student.getPanel().name())
                .year(student.getYear())
                .cgpa(student.getCgpa())
                .attendancePercent(student.getAttendancePercent())
                .email(student.getUser().getEmail())
                .phone(student.getPhone())
                .githubUrl(student.getGithubUrl())
                .linkedinUrl(student.getLinkedinUrl())
                .profilePhotoUrl(student.getProfilePhotoUrl())
                .skills(student.getSkills())
                .bio(student.getBio())
                .internshipCompany(student.getInternshipCompany())
                .internshipRole(student.getInternshipRole())
                .internshipStart(student.getInternshipStart())
                .internshipEnd(student.getInternshipEnd())
                .currentClubs(currentClubs)
                .clubHistory(clubHistory)
                .achievements(achievements)
                .academicRecords(records)
                .panelTeacherName(panelTeacherName)
                .panelTeacherEmail(panelTeacherEmail)
                .createdAt(student.getCreatedAt())
                .updatedAt(student.getUpdatedAt())
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

    private AchievementResponse toAchievementResponse(Achievement a) {
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

    private AcademicRecordResponse toAcademicRecordResponse(AcademicRecord r) {
        return AcademicRecordResponse.builder()
                .id(r.getId())
                .academicYear(r.getAcademicYear())
                .semester(r.getSemester())
                .sgpa(r.getSgpa())
                .cgpa(r.getCgpa())
                .attendancePercent(r.getAttendancePercent())
                .backlogs(r.getBacklogs())
                .remarks(r.getRemarks())
                .build();
    }

    private AnnouncementResponse toAnnouncementResponse(Announcement a) {
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

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "{}";
        }
    }
}
