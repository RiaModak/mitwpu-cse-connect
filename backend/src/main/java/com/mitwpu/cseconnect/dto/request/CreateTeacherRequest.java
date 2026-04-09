package com.mitwpu.cseconnect.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateTeacherRequest {
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 255)
    private String fullName;

    @NotBlank(message = "Employee ID is required")
    @Size(min = 3, max = 20, message = "Employee ID must be 3-20 characters")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Employee ID must be alphanumeric")
    private String employeeId;

    private String designation;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]+$",
             message = "Password must have uppercase, lowercase, digit, and special character")
    private String password;

    @Pattern(regexp = "^\\d{10}$", message = "Phone must be 10 digits")
    private String phone;
}
