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
public class ClubNoticeResponse {
    private Long id;
    private String title;
    private String body;
    private String postedByName;
    private LocalDateTime createdAt;
}
