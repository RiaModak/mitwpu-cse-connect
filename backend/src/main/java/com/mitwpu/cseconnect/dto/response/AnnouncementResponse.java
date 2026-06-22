package com.mitwpu.cseconnect.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementResponse {
    private Long id;
    private String title;
    private String body;
    private String postedByName;
    private String postedByRole;
    private String targetAudience;
    private String targetClubName;
    private String targetPanel;
    private Boolean isPinned;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
}
