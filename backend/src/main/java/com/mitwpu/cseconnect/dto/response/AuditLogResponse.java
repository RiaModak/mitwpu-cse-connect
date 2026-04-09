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
public class AuditLogResponse {
    private Long id;
    private String actorName;
    private String actorRole;
    private String action;
    private String entityName;
    private Long entityId;
    private String description;
    private String ipAddress;
    private String oldValue;
    private String newValue;
    private LocalDateTime createdAt;
}
