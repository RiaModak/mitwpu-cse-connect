package com.mitwpu.cseconnect.dto.request;

import lombok.Data;

@Data
public class UpdateMemberRequest {
    private String role;
    private String endYear;
    private Boolean isCurrent;
    private String notes;
}
