package com.mitwpu.cseconnect.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentSummaryResponse {
    private Long id;
    private String prn;
    private String fullName;
    private String panel;
    private Integer year;
    private BigDecimal cgpa;
    private BigDecimal attendancePercent;
    private String activeClubName;
    private String activeClubRole;
    private Long achievementsCount;
    private String profilePhotoUrl;
}
