package com.mitwpu.cseconnect.repository;

import com.mitwpu.cseconnect.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {
    List<Club> findByIsDeletedFalseAndIsActiveTrue();

    @Query("SELECT c FROM Club c WHERE c.isDeleted = false AND c.category = :category")
    List<Club> findByCategory(@Param("category") String category);

    @Query("SELECT c FROM Club c WHERE c.isDeleted = false")
    List<Club> findAllActive();

    @Query("SELECT COUNT(c) FROM Club c WHERE c.isDeleted = false AND c.isActive = true")
    long countActive();

    @Query("SELECT COUNT(c) FROM Club c WHERE c.isDeleted = false")
    long countAll();
}
