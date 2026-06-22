package com.mitwpu.cseconnect.repository;

import com.mitwpu.cseconnect.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByUserId(Long userId);
    Optional<Teacher> findByEmployeeId(String employeeId);

    @Query("SELECT t FROM Teacher t WHERE t.isDeleted = false")
    List<Teacher> findAllActive();

    @Query("SELECT COUNT(t) FROM Teacher t WHERE t.isDeleted = false")
    long countActive();
}
