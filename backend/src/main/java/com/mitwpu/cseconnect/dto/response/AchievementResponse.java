package com.mitwpu.cseconnect.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AchievementResponse {
    private Long id;
    private String title;
    private String category;
    private String description;
    private LocalDate dateOfAchievement;
    private String proofFileUrl;
    private String proofExternalUrl;
    private String issuingOrganization;
    private String status;
    private String verifiedByName;
    private LocalDateTime verifiedAt;
    private String rejectionReason;
    private String studentName;
    private String studentPrn;
    private Long studentId;
    private LocalDateTime createdAt;
}
