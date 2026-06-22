package com.mitwpu.cseconnect.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateAnnouncementRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Body is required")
    private String body;

    private String targetAudience = "ALL";
    private Long targetClubId;
    private String targetPanel = "ALL";
    private Boolean isPinned = false;
    private LocalDateTime expiresAt;
}
