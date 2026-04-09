package com.mitwpu.cseconnect.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipResponse {
    private Long id;
    private Long clubId;
    private String clubName;
    private String clubCategory;
    private String role;
    private String startYear;
    private String endYear;
    private Boolean isCurrent;
    private String joinedVia;
}
