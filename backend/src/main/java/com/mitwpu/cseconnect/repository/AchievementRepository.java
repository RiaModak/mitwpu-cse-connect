package com.mitwpu.cseconnect.repository;

import com.mitwpu.cseconnect.entity.Achievement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    List<Achievement> findByStudentIdAndIsDeletedFalse(Long studentId);

    @Query("SELECT a FROM Achievement a WHERE a.isDeleted = false AND a.status = :status")
    Page<Achievement> findByStatus(@Param("status") Achievement.AchievementStatus status, Pageable pageable);

    @Query("SELECT a FROM Achievement a WHERE a.isDeleted = false " +
           "AND (:category IS NULL OR a.category = :category) " +
           "AND (:status IS NULL OR a.status = :status)")
    Page<Achievement> findAllWithFilters(@Param("category") Achievement.AchievementCategory category,
                                         @Param("status") Achievement.AchievementStatus status,
                                         Pageable pageable);

    @Query("SELECT COUNT(a) FROM Achievement a WHERE a.isDeleted = false")
    long countAll();

    @Query("SELECT COUNT(a) FROM Achievement a WHERE a.isDeleted = false AND a.status = 'VERIFIED'")
    long countVerified();

    @Query("SELECT COUNT(a) FROM Achievement a WHERE a.isDeleted = false AND a.status = 'PENDING'")
    long countPending();

    @Query("SELECT a.category, COUNT(a) FROM Achievement a WHERE a.isDeleted = false GROUP BY a.category")
    List<Object[]> countByCategory();

    @Query("SELECT COUNT(a) FROM Achievement a WHERE a.isDeleted = false AND a.student.id = :studentId AND a.status = 'VERIFIED'")
    long countVerifiedByStudent(@Param("studentId") Long studentId);
}
