package com.mitwpu.cseconnect.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Student extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(unique = true, nullable = false, length = 10)
    private String prn;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Panel panel;

    @Column(nullable = false)
    private Integer year;

    @Column(precision = 4, scale = 2)
    private BigDecimal cgpa;

    @Column(name = "attendance_percent", precision = 5, scale = 2)
    private BigDecimal attendancePercent;

    @Column(name = "internship_company")
    private String internshipCompany;

    @Column(name = "internship_role")
    private String internshipRole;

    @Column(name = "internship_start")
    private LocalDate internshipStart;

    @Column(name = "internship_end")
    private LocalDate internshipEnd;

    @Column(length = 15)
    private String phone;

    @Column(name = "github_url", length = 500)
    private String githubUrl;

    @Column(name = "linkedin_url", length = 500)
    private String linkedinUrl;

    @Column(name = "profile_photo_url", length = 500)
    private String profilePhotoUrl;

    @Column(columnDefinition = "TEXT")
    private String skills;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private List<ClubMembership> clubMemberships = new ArrayList<>();

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private List<Achievement> achievements = new ArrayList<>();

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private List<AcademicRecord> academicRecords = new ArrayList<>();

    public enum Panel {
        A, B, C, D, E, F
    }
}
