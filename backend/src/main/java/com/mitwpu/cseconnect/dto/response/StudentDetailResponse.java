package com.mitwpu.cseconnect.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDetailResponse {
    private Long id;
    private String prn;
    private String fullName;
    private String panel;
    private Integer year;
    private BigDecimal cgpa;
    private BigDecimal attendancePercent;

    private String email;
    private String phone;
    private String githubUrl;
    private String linkedinUrl;
    private String profilePhotoUrl;
    private String skills;
    private String bio;

    private String internshipCompany;
    private String internshipRole;
    private LocalDate internshipStart;
    private LocalDate internshipEnd;

    private List<MembershipResponse> currentClubs;
    private List<MembershipResponse> clubHistory;
    private List<AchievementResponse> achievements;
    private List<AcademicRecordResponse> academicRecords;

    private String panelTeacherName;
    private String panelTeacherEmail;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
