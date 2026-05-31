package com.example.selectcoursesystem.controller;

import com.example.selectcoursesystem.entity.Grade;
import com.example.selectcoursesystem.service.GradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/grade")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;

    // 学生查询自己成绩
    @GetMapping("/my/{studentId}")
    public ResponseEntity<List<Grade>> myGrades(@PathVariable Integer studentId) {
        return ResponseEntity.ok(gradeService.getMyGrades(studentId));
    }

    // 教师录入成绩
    @PostMapping("/save")
    public ResponseEntity<?> saveGrade(@RequestBody Map<String, Object> body) {
        try {
            Integer enrollmentId = (Integer) body.get("enrollmentId");
            Double score = Double.valueOf(body.get("score").toString());
            return ResponseEntity.ok(gradeService.saveGrade(enrollmentId, score));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}