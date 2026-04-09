package com.mitwpu.cseconnect.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherResponse {
    private Long id;
    private Long userId;
    private String fullName;
    private String employeeId;
    private String designation;
    private String email;
    private String phone;
    private List<String> currentPanels;
    private LocalDateTime createdAt;
}
