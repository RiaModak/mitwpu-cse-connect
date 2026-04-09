package com.mitwpu.cseconnect.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateTeacherRequest {
    @Size(min = 2, max = 255)
    private String fullName;
    private String designation;
    @Pattern(regexp = "^\\d{10}$", message = "Phone must be 10 digits")
    private String phone;
}
