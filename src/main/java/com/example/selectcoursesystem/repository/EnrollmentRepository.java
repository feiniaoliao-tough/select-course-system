package com.example.selectcoursesystem.repository;

import com.example.selectcoursesystem.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    List<Enrollment> findByStudentStudentId(Integer studentId);
    List<Enrollment> findByCourseCourseId(Integer courseId);
    Optional<Enrollment> findByStudentStudentIdAndCourseCourseId(Integer studentId, Integer courseId);
    boolean existsByStudentStudentIdAndCourseCourseId(Integer studentId, Integer courseId);
}
