package com.example.selectcoursesystem.repository;

import com.example.selectcoursesystem.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Integer> {
    Optional<Student> findByUserUserId(Integer userId);
}