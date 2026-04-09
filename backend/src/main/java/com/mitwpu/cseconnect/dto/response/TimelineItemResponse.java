package com.mitwpu.cseconnect.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimelineItemResponse {
    private String type;
    private LocalDate date;
    private String title;
    private String subtitle;
    private String category;
    private String status;
    private Map<String, String> metadata;
}
