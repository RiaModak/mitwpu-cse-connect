package com.mitwpu.cseconnect.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDetailResponse {
    private Long membershipId;
    private Long studentId;
    private String prn;
    private String studentName;
    private String profilePhotoUrl;
    private String role;
    private String startYear;
    private String endYear;
    private Boolean isCurrent;
    private String panel;
    private Integer year;
}
