package com.mitwpu.cseconnect.repository;

import com.mitwpu.cseconnect.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    @Query("SELECT a FROM Announcement a WHERE a.isDeleted = false " +
           "AND (a.targetAudience = 'ALL' " +
           "OR (a.targetAudience = 'STUDENTS' AND (a.targetPanel = 'ALL' OR a.targetPanel = :panel)) " +
           "OR (a.targetAudience = 'CLUB' AND a.targetClub.id IN :clubIds)) " +
           "ORDER BY a.isPinned DESC, a.createdAt DESC")
    List<Announcement> findVisibleForStudent(@Param("panel") Announcement.TargetPanel panel,
                                              @Param("clubIds") List<Long> clubIds);

    @Query("SELECT a FROM Announcement a WHERE a.isDeleted = false " +
           "AND (a.targetAudience = 'ALL' OR a.targetAudience = 'TEACHERS') " +
           "ORDER BY a.isPinned DESC, a.createdAt DESC")
    List<Announcement> findVisibleForTeacher();

    @Query("SELECT a FROM Announcement a WHERE a.isDeleted = false " +
           "ORDER BY a.isPinned DESC, a.createdAt DESC")
    List<Announcement> findAllActive();

    @Query("SELECT COUNT(a) FROM Announcement a WHERE a.isDeleted = false AND a.postedBy.id = :userId")
    long countByPostedBy(@Param("userId") Long userId);
}
