package com.mitwpu.cseconnect.service;

import com.mitwpu.cseconnect.dto.request.AssignPanelRequest;
import com.mitwpu.cseconnect.dto.request.CreateTeacherRequest;
import com.mitwpu.cseconnect.dto.request.UpdateTeacherRequest;
import com.mitwpu.cseconnect.dto.response.DashboardStatsResponse;
import com.mitwpu.cseconnect.dto.response.StudentSummaryResponse;
import com.mitwpu.cseconnect.dto.response.TeacherResponse;

import java.util.List;

public interface TeacherService {
    List<TeacherResponse> getAll();
    TeacherResponse getById(Long id);
    TeacherResponse create(CreateTeacherRequest request, Long actorUserId, String ip);
    TeacherResponse update(Long id, UpdateTeacherRequest request, Long actorUserId, String ip);
    void delete(Long id, Long actorUserId, String ip);
    void assignPanel(AssignPanelRequest request, Long actorUserId, String ip);
    List<StudentSummaryResponse> getStudentsByTeacher(Long teacherUserId);
    List<StudentSummaryResponse> getStudentsByPanel(String panel);
    DashboardStatsResponse getTeacherDashboard(Long userId);
}
