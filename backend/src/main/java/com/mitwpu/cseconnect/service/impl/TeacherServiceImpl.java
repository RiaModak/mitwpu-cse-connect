package com.mitwpu.cseconnect.service.impl;

import com.mitwpu.cseconnect.dto.request.AssignPanelRequest;
import com.mitwpu.cseconnect.dto.request.CreateTeacherRequest;
import com.mitwpu.cseconnect.dto.request.UpdateTeacherRequest;
import com.mitwpu.cseconnect.dto.response.*;
import com.mitwpu.cseconnect.entity.*;
import com.mitwpu.cseconnect.exception.DuplicateResourceException;
import com.mitwpu.cseconnect.exception.ResourceNotFoundException;
import com.mitwpu.cseconnect.repository.*;
import com.mitwpu.cseconnect.service.AuditService;
import com.mitwpu.cseconnect.service.TeacherService;
import com.mitwpu.cseconnect.entity.ClubJoinRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherPanelAssignmentRepository panelAssignmentRepository;
    private final ClubMembershipRepository membershipRepository;
    private final AchievementRepository achievementRepository;
    private final AnnouncementRepository announcementRepository;
    private final ClubJoinRequestRepository joinRequestRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    @Override
    public List<TeacherResponse> getAll() {
        return teacherRepository.findAllActive().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TeacherResponse getById(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with ID: " + id));
        return toResponse(teacher);
    }

    @Override
    @Transactional
    public TeacherResponse create(CreateTeacherRequest request, Long actorUserId, String ip) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + request.getEmail());
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.TEACHER);
        user.setIsActive(true);
        user.setIsDeleted(false);
        user = userRepository.save(user);

        Teacher teacher = new Teacher();
        teacher.setUser(user);
        teacher.setFullName(request.getFullName());
        teacher.setEmployeeId(request.getEmployeeId());
        teacher.setDesignation(request.getDesignation());
        teacher.setPhone(request.getPhone());
        teacher.setCreatedBy(actorUserId);
        teacher.setIsDeleted(false);
        teacher = teacherRepository.save(teacher);

        auditService.log(actorUserId, null, null, AuditLog.AuditAction.CREATE,
                "Teacher", teacher.getId(), null, null,
                "Created teacher: " + teacher.getFullName(), ip);

        return toResponse(teacher);
    }

    @Override
    @Transactional
    public TeacherResponse update(Long id, UpdateTeacherRequest request, Long actorUserId, String ip) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with ID: " + id));

        if (request.getFullName() != null) teacher.setFullName(request.getFullName());
        if (request.getDesignation() != null) teacher.setDesignation(request.getDesignation());
        if (request.getPhone() != null) teacher.setPhone(request.getPhone());
        teacher.setUpdatedBy(actorUserId);
        teacher = teacherRepository.save(teacher);

        auditService.log(actorUserId, null, null, AuditLog.AuditAction.UPDATE,
                "Teacher", teacher.getId(), null, null,
                "Updated teacher: " + teacher.getFullName(), ip);

        return toResponse(teacher);
    }

    @Override
    @Transactional
    public void delete(Long id, Long actorUserId, String ip) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with ID: " + id));
        teacher.setIsDeleted(true);
        teacher.setDeletedAt(LocalDateTime.now());
        teacherRepository.save(teacher);

        User user = teacher.getUser();
        user.setIsDeleted(true);
        user.setDeletedAt(LocalDateTime.now());
        user.setIsActive(false);
        userRepository.save(user);

        auditService.log(actorUserId, null, null, AuditLog.AuditAction.DELETE,
                "Teacher", teacher.getId(), null, null,
                "Deleted teacher: " + teacher.getFullName(), ip);
    }

    @Override
    @Transactional
    public void assignPanel(AssignPanelRequest request, Long actorUserId, String ip) {
        Teacher teacher = teacherRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        TeacherPanelAssignment assignment = new TeacherPanelAssignment();
        assignment.setTeacher(teacher);
        assignment.setPanel(Student.Panel.valueOf(request.getPanel()));
        assignment.setAcademicYear(request.getAcademicYear());
        assignment.setIsCurrent(true);
        assignment.setIsDeleted(false);
        panelAssignmentRepository.save(assignment);

        auditService.log(actorUserId, null, null, AuditLog.AuditAction.CREATE,
                "PanelAssignment", assignment.getId(), null, null,
                "Assigned " + teacher.getFullName() + " to Panel " + request.getPanel(), ip);
    }

    @Override
    public List<StudentSummaryResponse> getStudentsByTeacher(Long teacherUserId) {
        Teacher teacher = teacherRepository.findByUserId(teacherUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        List<TeacherPanelAssignment> assignments = panelAssignmentRepository.findByTeacherIdAndIsCurrent(teacher.getId(), true);
        List<StudentSummaryResponse> result = new ArrayList<>();

        for (TeacherPanelAssignment assignment : assignments) {
            List<Student> students = studentRepository.findByPanel(assignment.getPanel());
            for (Student student : students) {
                result.add(toStudentSummary(student));
            }
        }

        return result;
    }

    @Override
    public List<StudentSummaryResponse> getStudentsByPanel(String panel) {
        Student.Panel panelEnum = Student.Panel.valueOf(panel);
        return studentRepository.findByPanel(panelEnum).stream()
                .map(this::toStudentSummary)
                .collect(Collectors.toList());
    }

    @Override
    public DashboardStatsResponse getTeacherDashboard(Long userId) {
        Teacher teacher = teacherRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        List<TeacherPanelAssignment> assignments = panelAssignmentRepository.findByTeacherIdAndIsCurrent(teacher.getId(), true);
        List<StudentSummaryResponse> panelStudents = new ArrayList<>();

        for (TeacherPanelAssignment assignment : assignments) {
            List<Student> students = studentRepository.findByPanel(assignment.getPanel());
            for (Student student : students) {
                panelStudents.add(toStudentSummary(student));
            }
        }

        long pendingVerifications = achievementRepository.countPending();
        long announcementsPosted = announcementRepository.countByPostedBy(userId);

        List<ClubJoinRequest> pendingJoinReqs = joinRequestRepository.findByStatusAndIsDeletedFalse(
                ClubJoinRequest.JoinRequestStatus.PENDING);
        List<ClubJoinRequestResponse> joinRequestResponses = pendingJoinReqs.stream()
                .map(r -> ClubJoinRequestResponse.builder()
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
                        .createdAt(r.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return DashboardStatsResponse.builder()
                .totalStudentsInPanel((long) panelStudents.size())
                .pendingVerifications(pendingVerifications)
                .announcementsPosted(announcementsPosted)
                .panelStudents(panelStudents)
                .pendingJoinRequests((long) pendingJoinReqs.size())
                .joinRequests(joinRequestResponses)
                .build();
    }

    private TeacherResponse toResponse(Teacher teacher) {
        List<TeacherPanelAssignment> assignments = panelAssignmentRepository.findByTeacherIdAndIsCurrent(teacher.getId(), true);
        List<String> panels = assignments.stream()
                .map(a -> a.getPanel().name())
                .collect(Collectors.toList());

        return TeacherResponse.builder()
                .id(teacher.getId())
                .userId(teacher.getUser().getId())
                .fullName(teacher.getFullName())
                .employeeId(teacher.getEmployeeId())
                .designation(teacher.getDesignation())
                .email(teacher.getUser().getEmail())
                .phone(teacher.getPhone())
                .currentPanels(panels)
                .createdAt(teacher.getCreatedAt())
                .build();
    }

    private StudentSummaryResponse toStudentSummary(Student student) {
        List<ClubMembership> currentMemberships = membershipRepository.findByStudentIdAndIsCurrentTrueAndIsDeletedFalse(student.getId());
        String activeClubName = null;
        String activeClubRole = null;
        if (!currentMemberships.isEmpty()) {
            activeClubName = currentMemberships.get(0).getClub().getName();
            activeClubRole = currentMemberships.get(0).getRole().name();
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
}
