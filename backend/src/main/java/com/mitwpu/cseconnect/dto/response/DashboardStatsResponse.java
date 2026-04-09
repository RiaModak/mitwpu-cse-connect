package com.mitwpu.cseconnect.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    // Admin stats
    private Long totalStudents;
    private Long totalTeachers;
    private Long totalClubs;
    private Long activeClubs;
    private Long totalAchievements;
    private Long verifiedAchievements;
    private Long pendingVerifications;
    private Map<String, Long> achievementsByCategory;
    private Map<String, Long> studentsByYear;
    private Map<String, Long> studentsByPanel;
    private List<AuditLogResponse> recentAuditLogs;
    private Long auditEntriesToday;

    // Teacher stats
    private Long totalStudentsInPanel;
    private Long announcementsPosted;
    private List<StudentSummaryResponse> panelStudents;
    private Long pendingJoinRequests;
    private List<ClubJoinRequestResponse> joinRequests;

    // Student stats
    private java.math.BigDecimal cgpa;
    private java.math.BigDecimal attendancePercent;
    private Long activeClubsCount;
    private Long achievementsCount;
    private Long verifiedAchievementsCount;
    private List<MembershipResponse> currentClubs;
    private List<AnnouncementResponse> recentAnnouncements;
    private List<AcademicRecordResponse> academicRecords;
}
