package com.mitwpu.cseconnect.service;

import com.mitwpu.cseconnect.entity.AuditLog;

public interface AuditService {
    void log(Long actorUserId, String actorName, String actorRole,
             AuditLog.AuditAction action, String entityName, Long entityId,
             String oldValue, String newValue, String description, String ipAddress);
}
