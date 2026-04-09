package com.mitwpu.cseconnect.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AssignPanelRequest {
    @NotNull(message = "Teacher ID is required")
    private Long teacherId;

    @NotBlank(message = "Panel is required")
    @Pattern(regexp = "^[A-F]$", message = "Panel must be A, B, C, D, E, or F")
    private String panel;

    @NotBlank(message = "Academic year is required")
    @Pattern(regexp = "^\\d{4}-\\d{4}$", message = "Academic year must be in YYYY-YYYY format")
    private String academicYear;
}
