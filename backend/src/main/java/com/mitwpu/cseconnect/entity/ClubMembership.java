package com.mitwpu.cseconnect.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "club_memberships")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClubMembership extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClubRole role = ClubRole.MEMBER;

    @Column(name = "start_year", nullable = false, length = 9)
    private String startYear;

    @Column(name = "end_year", length = 9)
    private String endYear;

    @Column(name = "is_current")
    private Boolean isCurrent = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "joined_via")
    private JoinedVia joinedVia = JoinedVia.APPLICATION;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    public enum ClubRole {
        MEMBER, SECRETARY, VICE_PRESIDENT, PRESIDENT
    }

    public enum JoinedVia {
        APPLICATION, DIRECT, SELECTED
    }
}
