package com.mitwpu.cseconnect.repository;

import com.mitwpu.cseconnect.entity.ClubNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClubNoticeRepository extends JpaRepository<ClubNotice, Long> {
    List<ClubNotice> findByClubIdAndIsDeletedFalseOrderByCreatedAtDesc(Long clubId);
}
