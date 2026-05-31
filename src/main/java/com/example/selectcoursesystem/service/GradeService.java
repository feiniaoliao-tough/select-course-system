package com.example.selectcoursesystem.service;

import com.example.selectcoursesystem.entity.Grade;
import com.example.selectcoursesystem.repository.EnrollmentRepository;
import com.example.selectcoursesystem.repository.GradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository gradeRepository;
    private final EnrollmentRepository enrollmentRepository;

    // 查询学生所有成绩
    public List<Grade> getMyGrades(Integer studentId) {
        return enrollmentRepository.findByStudentStudentId(studentId)
                .stream()
                .map(e -> gradeRepository.findByEnrollmentEnrollmentId(e.getEnrollmentId()))
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .collect(Collectors.toList());
    }

    // 教师录入成绩
    public Grade saveGrade(Integer enrollmentId, Double score) {
        Grade grade = gradeRepository
                .findByEnrollmentEnrollmentId(enrollmentId)
                .orElse(new Grade());

        grade.setEnrollment(enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("选课记录不存在")));
        grade.setScore(score);
        grade.setGradeLevel(calcLevel(score));
        return gradeRepository.save(grade);
    }

    private String calcLevel(Double score) {
        if (score >= 90) return "A";
        if (score >= 80) return "B";
        if (score >= 70) return "C";
        if (score >= 60) return "D";
        return "F";
    }
}