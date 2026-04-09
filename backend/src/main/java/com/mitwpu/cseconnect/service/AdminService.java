package com.mitwpu.cseconnect.service;

import com.mitwpu.cseconnect.dto.request.ResetPasswordRequest;
import com.mitwpu.cseconnect.dto.response.AuditLogResponse;
import com.mitwpu.cseconnect.dto.response.DashboardStatsResponse;
import com.mitwpu.cseconnect.dto.response.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface AdminService {
    DashboardStatsResponse getDashboardStats();
    PagedResponse<AuditLogResponse> getAuditLogs(Pageable pageable);
    void resetStudentPassword(String prn, ResetPasswordRequest request, Long actorUserId, String ip);
    void resetTeacherPassword(Long id, ResetPasswordRequest request, Long actorUserId, String ip);
}
