package com.mitwpu.cseconnect.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AddMemberRequest {
    @NotBlank(message = "Student PRN is required")
    @Pattern(regexp = "^\\d{10}$", message = "PRN must be exactly 10 digits")
    private String studentPrn;

    private String role = "MEMBER";

    @NotBlank(message = "Start year is required")
    @Pattern(regexp = "^\\d{4}-\\d{4}$", message = "Start year must be YYYY-YYYY format")
    private String startYear;

    private String joinedVia = "APPLICATION";
    private String notes;
}
