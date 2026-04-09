package com.mitwpu.cseconnect.dto.request;

import lombok.Data;

@Data
public class UpdateClubRequest {
    private String name;
    private String description;
    private String category;
    private String logoUrl;
    private Integer foundedYear;
    private String facultyAdvisor;
}
