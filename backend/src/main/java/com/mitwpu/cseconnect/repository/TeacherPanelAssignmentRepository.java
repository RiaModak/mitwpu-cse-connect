package com.mitwpu.cseconnect.repository;

import com.mitwpu.cseconnect.entity.Student;
import com.mitwpu.cseconnect.entity.TeacherPanelAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherPanelAssignmentRepository extends JpaRepository<TeacherPanelAssignment, Long> {
    List<TeacherPanelAssignment> findByTeacherIdAndIsCurrent(Long teacherId, boolean isCurrent);
    Optional<TeacherPanelAssignment> findByPanelAndIsCurrentAndAcademicYear(Student.Panel panel, boolean isCurrent, String academicYear);
    List<TeacherPanelAssignment> findByTeacherIdAndIsDeletedFalse(Long teacherId);
    Optional<TeacherPanelAssignment> findByPanelAndIsCurrentTrue(Student.Panel panel);
}
