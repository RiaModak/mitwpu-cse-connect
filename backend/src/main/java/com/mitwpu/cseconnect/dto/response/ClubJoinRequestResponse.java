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
public class ClubJoinRequestResponse {
    private Long id;
    private Long clubId;
    private String clubName;
    private String clubCategory;
    private Long studentId;
    private String studentPrn;
    private String studentName;
    private String studentPanel;
    private Integer studentYear;
    private String status;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;
}
