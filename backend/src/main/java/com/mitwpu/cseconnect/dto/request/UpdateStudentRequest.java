package com.mitwpu.cseconnect.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UpdateStudentRequest {
    @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
    private String fullName;

    @Pattern(regexp = "^[A-F]$", message = "Panel must be A, B, C, D, E, or F")
    private String panel;

    @Min(value = 1, message = "Year must be between 1 and 4")
    @Max(value = 4, message = "Year must be between 1 and 4")
    private Integer year;

    @DecimalMin(value = "0.0", message = "CGPA must be between 0.0 and 10.0")
    @DecimalMax(value = "10.0", message = "CGPA must be between 0.0 and 10.0")
    private BigDecimal cgpa;

    @DecimalMin(value = "0.0", message = "Attendance must be between 0.0 and 100.0")
    @DecimalMax(value = "100.0", message = "Attendance must be between 0.0 and 100.0")
    private BigDecimal attendancePercent;

    @Pattern(regexp = "^\\d{10}$", message = "Phone must be 10 digits")
    private String phone;

    private String githubUrl;
    private String linkedinUrl;
    private String internshipCompany;
    private String internshipRole;
    private LocalDate internshipStart;
    private LocalDate internshipEnd;
    private String skills;
    private String bio;
    private String profilePhotoUrl;
}
