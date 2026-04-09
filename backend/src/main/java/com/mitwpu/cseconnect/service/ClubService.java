package com.mitwpu.cseconnect.service;

import com.mitwpu.cseconnect.dto.request.*;
import com.mitwpu.cseconnect.dto.response.*;

import java.util.List;

public interface ClubService {
    List<ClubSummaryResponse> getAll(String category, String search);
    ClubDetailResponse getById(Long id);
    ClubSummaryResponse create(CreateClubRequest request, Long actorUserId, String ip);
    ClubSummaryResponse update(Long id, UpdateClubRequest request, Long actorUserId, String ip);
    void deactivate(Long id, Long actorUserId, String ip);
    MembershipResponse addMember(Long clubId, AddMemberRequest request, Long actorUserId, String ip);
    MembershipResponse updateMember(Long clubId, Long membershipId, UpdateMemberRequest request, Long actorUserId, String ip);
    void removeMember(Long clubId, Long membershipId, Long actorUserId, String ip);
    ClubNoticeResponse postNotice(Long clubId, PostClubNoticeRequest request, Long studentId);
    List<ClubNoticeResponse> getNotices(Long clubId);
    boolean isClubHead(Long studentId, Long clubId);
    boolean isCurrentMember(Long studentId, Long clubId);
}
