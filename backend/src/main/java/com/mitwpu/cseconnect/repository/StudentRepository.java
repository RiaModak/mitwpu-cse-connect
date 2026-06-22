package com.mitwpu.cseconnect.repository;

import com.mitwpu.cseconnect.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByPrn(String prn);
    Optional<Student> findByUserId(Long userId);

    @Query("SELECT s FROM Student s WHERE s.panel = :panel AND s.isDeleted = false")
    List<Student> findByPanel(@Param("panel") Student.Panel panel);

    @Query("SELECT s FROM Student s WHERE s.year = :year AND s.isDeleted = false")
    List<Student> findByYear(@Param("year") int year);

    @Query("SELECT s FROM Student s WHERE s.isDeleted = false " +
           "AND (:prn IS NULL OR s.prn LIKE %:prn%) " +
           "AND (:name IS NULL OR LOWER(s.fullName) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND (:panel IS NULL OR s.panel = :panel) " +
           "AND (:year IS NULL OR s.year = :year)")
    Page<Student> searchStudents(@Param("prn") String prn,
                                 @Param("name") String name,
                                 @Param("panel") Student.Panel panel,
                                 @Param("year") Integer year,
                                 Pageable pageable);

    @Query("SELECT s FROM Student s WHERE s.isDeleted = false")
    List<Student> findAllActive();

    @Query("SELECT s FROM Student s WHERE s.isDeleted = :showDeleted OR s.isDeleted = false")
    Page<Student> findAllWithDeleted(@Param("showDeleted") boolean showDeleted, Pageable pageable);

    @Query("SELECT COUNT(s) FROM Student s WHERE s.isDeleted = false")
    long countActive();

    @Query("SELECT COUNT(s) FROM Student s WHERE s.isDeleted = false AND s.year = :year")
    long countByYear(@Param("year") int year);

    @Query("SELECT COUNT(s) FROM Student s WHERE s.isDeleted = false AND s.panel = :panel")
    long countByPanel(@Param("panel") Student.Panel panel);

    boolean existsByPrn(String prn);
}
