package com.mitwpu.cseconnect.service.impl;

import com.mitwpu.cseconnect.entity.AuditLog;
import com.mitwpu.cseconnect.repository.AuditLogRepository;
import com.mitwpu.cseconnect.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;

    @Override
    @Async
    public void log(Long actorUserId, String actorName, String actorRole,
                    AuditLog.AuditAction action, String entityName, Long entityId,
                    String oldValue, String newValue, String description, String ipAddress) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setActorUserId(actorUserId);
            auditLog.setActorName(actorName);
            auditLog.setActorRole(actorRole);
            auditLog.setAction(action);
            auditLog.setEntityName(entityName);
            auditLog.setEntityId(entityId);
            auditLog.setOldValue(oldValue);
            auditLog.setNewValue(newValue);
            auditLog.setDescription(description);
            auditLog.setIpAddress(ipAddress);
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Failed to save audit log: {}", e.getMessage());
        }
    }
}
