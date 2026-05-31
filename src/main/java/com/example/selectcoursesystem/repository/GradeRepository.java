package com.example.selectcoursesystem.repository;

import com.example.selectcoursesystem.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GradeRepository extends JpaRepository<Grade, Integer> {
    Optional<Grade> findByEnrollmentEnrollmentId(Integer enrollmentId);
}