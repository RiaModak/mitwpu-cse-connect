package com.mitwpu.cseconnect.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubDetailResponse {
    private Long id;
    private String name;
    private String description;
    private String category;
    private String logoUrl;
    private Integer foundedYear;
    private String facultyAdvisor;
    private Boolean isActive;
    private Long currentMemberCount;
    private String presidentName;
    private String presidentPrn;

    private List<MemberDetailResponse> currentMembers;
    private List<MemberDetailResponse> allTimeMembers;
    private List<ClubNoticeResponse> recentNotices;
}
