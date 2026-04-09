package com.mitwpu.cseconnect.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateStudentRequest {
    @NotBlank(message = "PRN is required")
    @Pattern(regexp = "^\\d{10}$", message = "PRN must be exactly 10 digits")
    private String prn;

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
    private String fullName;

    @NotBlank(message = "Panel is required")
    @Pattern(regexp = "^[A-F]$", message = "Panel must be A, B, C, D, E, or F")
    private String panel;

    @NotNull(message = "Year is required")
    @Min(value = 1, message = "Year must be between 1 and 4")
    @Max(value = 4, message = "Year must be between 1 and 4")
    private Integer year;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]+$",
             message = "Password must have uppercase, lowercase, digit, and special character")
    private String password;

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
}
