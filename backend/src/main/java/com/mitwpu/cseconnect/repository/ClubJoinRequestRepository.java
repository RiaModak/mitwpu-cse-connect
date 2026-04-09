package com.mitwpu.cseconnect.repository;

import com.mitwpu.cseconnect.entity.ClubJoinRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClubJoinRequestRepository extends JpaRepository<ClubJoinRequest, Long> {
    List<ClubJoinRequest> findByStatusAndIsDeletedFalse(ClubJoinRequest.JoinRequestStatus status);
    List<ClubJoinRequest> findByStudentIdAndClubIdAndStatusAndIsDeletedFalse(Long studentId, Long clubId, ClubJoinRequest.JoinRequestStatus status);
    List<ClubJoinRequest> findByStudentIdAndIsDeletedFalse(Long studentId);
    List<ClubJoinRequest> findByClubIdAndStatusAndIsDeletedFalse(Long clubId, ClubJoinRequest.JoinRequestStatus status);
    boolean existsByStudentIdAndClubIdAndStatusAndIsDeletedFalse(Long studentId, Long clubId, ClubJoinRequest.JoinRequestStatus status);
}
