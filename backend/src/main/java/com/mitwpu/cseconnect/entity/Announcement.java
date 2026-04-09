package com.mitwpu.cseconnect.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "announcements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Announcement extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posted_by", nullable = false)
    private User postedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_audience", nullable = false)
    private TargetAudience targetAudience = TargetAudience.ALL;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_club_id")
    private Club targetClub;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_panel")
    private TargetPanel targetPanel = TargetPanel.ALL;

    @Column(name = "is_pinned")
    private Boolean isPinned = false;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    public enum TargetAudience {
        ALL, STUDENTS, TEACHERS, CLUB
    }

    public enum TargetPanel {
        A, B, C, D, E, F, ALL
    }
}
