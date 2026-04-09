package com.mitwpu.cseconnect.repository;

import com.mitwpu.cseconnect.entity.ClubMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClubMembershipRepository extends JpaRepository<ClubMembership, Long> {
    List<ClubMembership> findByStudentIdAndIsDeletedFalse(Long studentId);
    List<ClubMembership> findByClubIdAndIsCurrentTrueAndIsDeletedFalse(Long clubId);
    List<ClubMembership> findByClubIdAndIsDeletedFalse(Long clubId);
    Optional<ClubMembership> findByStudentIdAndClubIdAndIsCurrentTrue(Long studentId, Long clubId);
    boolean existsByStudentIdAndClubIdAndIsCurrentTrue(Long studentId, Long clubId);
    List<ClubMembership> findByStudentIdAndIsCurrentTrueAndIsDeletedFalse(Long studentId);
}
