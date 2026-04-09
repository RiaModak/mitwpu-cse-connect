package com.mitwpu.cseconnect.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddAcademicRecordRequest {
    @NotBlank(message = "Academic year is required")
    @Pattern(regexp = "^\\d{4}-\\d{4}$", message = "Academic year must be YYYY-YYYY format")
    private String academicYear;

    @NotNull(message = "Semester is required")
    @Min(value = 1, message = "Semester must be between 1 and 8")
    @Max(value = 8, message = "Semester must be between 1 and 8")
    private Integer semester;

    @DecimalMin(value = "0.0") @DecimalMax(value = "10.0")
    private BigDecimal sgpa;

    @DecimalMin(value = "0.0") @DecimalMax(value = "10.0")
    private BigDecimal cgpa;

    @DecimalMin(value = "0.0") @DecimalMax(value = "100.0")
    private BigDecimal attendancePercent;

    @Min(0)
    private Integer backlogs = 0;

    private String remarks;
}
