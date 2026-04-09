package com.mitwpu.cseconnect.service;

import com.mitwpu.cseconnect.dto.request.SubmitAchievementRequest;
import com.mitwpu.cseconnect.dto.request.VerifyAchievementRequest;
import com.mitwpu.cseconnect.dto.response.AchievementResponse;
import com.mitwpu.cseconnect.dto.response.PagedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AchievementService {
    PagedResponse<AchievementResponse> getAll(String category, String status, Pageable pageable);
    List<AchievementResponse> getByStudent(String prn);
    AchievementResponse submit(SubmitAchievementRequest request, MultipartFile proofFile, Long studentId);
    AchievementResponse verify(Long id, VerifyAchievementRequest request, Long actorUserId, String ip);
    void delete(Long id, Long actorUserId, String ip);
    AchievementResponse getById(Long id);
}
