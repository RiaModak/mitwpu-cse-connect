package com.mitwpu.cseconnect.repository;

import com.mitwpu.cseconnect.entity.AcademicRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AcademicRecordRepository extends JpaRepository<AcademicRecord, Long> {
    List<AcademicRecord> findByStudentIdOrderByAcademicYearDescSemesterDesc(Long studentId);
}
