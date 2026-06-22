package com.mitwpu.cseconnect.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyAchievementRequest {
    @NotBlank(message = "Status is required (VERIFIED or REJECTED)")
    private String status;
    private String rejectionReason;
}
