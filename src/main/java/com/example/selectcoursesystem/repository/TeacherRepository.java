package com.example.selectcoursesystem.repository;

import com.example.selectcoursesystem.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Integer> {
    Optional<Teacher> findByUserUserId(Integer userId);
}