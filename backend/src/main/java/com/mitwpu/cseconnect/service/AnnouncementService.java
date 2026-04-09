package com.mitwpu.cseconnect.service;

import com.mitwpu.cseconnect.dto.request.CreateAnnouncementRequest;
import com.mitwpu.cseconnect.dto.response.AnnouncementResponse;

import java.util.List;

public interface AnnouncementService {
    List<AnnouncementResponse> getVisible(Long userId);
    AnnouncementResponse create(CreateAnnouncementRequest request, Long actorUserId, String ip);
    void delete(Long id, Long actorUserId, String ip);
}
