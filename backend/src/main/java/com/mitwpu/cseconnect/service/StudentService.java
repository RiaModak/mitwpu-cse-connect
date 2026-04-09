package com.mitwpu.cseconnect.service;

import com.mitwpu.cseconnect.dto.request.AddAcademicRecordRequest;
import com.mitwpu.cseconnect.dto.request.CreateStudentRequest;
import com.mitwpu.cseconnect.dto.request.UpdateStudentRequest;
import com.mitwpu.cseconnect.dto.response.*;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StudentService {
    PagedResponse<StudentSummaryResponse> getAll(String panel, Integer year, String search, Pageable pageable);
    StudentDetailResponse getByPrn(String prn);
    StudentDetailResponse create(CreateStudentRequest request, Long actorUserId, String ip);
    StudentDetailResponse update(String prn, UpdateStudentRequest request, Long actorUserId, String ip);
    void delete(String prn, Long actorUserId, String ip);
    void resetPassword(String prn, String newPassword, Long actorUserId, String ip);
    List<MembershipResponse> getClubHistory(String prn);
    List<AchievementResponse> getAchievements(String prn);
    List<TimelineItemResponse> getTimeline(String prn);
    BulkImportResultResponse bulkImport(MultipartFile csv, Long actorUserId, String ip);
    AcademicRecordResponse addAcademicRecord(String prn, AddAcademicRecordRequest request, Long actorUserId, String ip);
    DashboardStatsResponse getStudentDashboard(Long userId);
}
