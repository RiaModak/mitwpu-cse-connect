package com.mitwpu.cseconnect.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "achievements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Achievement extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AchievementCategory category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "date_of_achievement", nullable = false)
    private LocalDate dateOfAchievement;

    @Column(name = "proof_file_url", length = 500)
    private String proofFileUrl;

    @Column(name = "proof_external_url", length = 500)
    private String proofExternalUrl;

    @Column(name = "issuing_organization")
    private String issuingOrganization;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AchievementStatus status = AchievementStatus.PENDING;

    @Column(name = "verified_by")
    private Long verifiedBy;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    public enum AchievementCategory {
        HACKATHON, CERTIFICATION, PROJECT, PAPER_PUBLICATION, COMPETITION, INTERNSHIP, OTHER
    }

    public enum AchievementStatus {
        PENDING, VERIFIED, REJECTED
    }
}
